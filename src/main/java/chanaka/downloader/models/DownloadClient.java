package chanaka.downloader.models;

import chanaka.downloader.controllers.MainController;
import chanaka.downloader.util.DownloadStatus;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.mime.MimeType;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;

@Component
@Scope("prototype")
public class DownloadClient {

    DownloadableURL downloadableURL;

    public DownloadClient(DownloadableURL downloadableURL) {
        this.downloadableURL = downloadableURL;
    }

    public void startDownload() {
        handleDownload(downloadableURL,false);
    }

    public void resumeDownload() {
        handleDownload(downloadableURL,true);
    }

    private void handleDownload(DownloadableURL downloadableURL, boolean isResume) {
        try {
            CloseableHttpResponse response = sendRequest(downloadableURL);

            if (!isResume) {
                long fileSize = getContentLength(response);
                String fileName = getFilenameWithExtension(response);
                System.out.println("setting file name" + fileName);
                downloadableURL.setFileName(fileName);
                downloadableURL.setFileSize(fileSize);
            }

            String saveFilePath = MainController.getSaveLocation() + File.separator + downloadableURL.getFileName();
            InputStream inputStream = response.getEntity().getContent();
            FileOutputStream outputStream = new FileOutputStream(saveFilePath, true);

            downloadableURL.updateView();

            // Copy the input stream to the output stream
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                if (downloadableURL.getDownloadStatus().equals(DownloadStatus.PAUSED)) {
                    response.close();
                    break;
                }
                System.out.println("downloading...");
                outputStream.write(buffer, 0, bytesRead);
                downloadableURL.updateDownloadedSize(buffer.length);
                downloadableURL.setProgressBar();
            }

            // Close streams
            inputStream.close();
            outputStream.close();

            // Release the connection
            EntityUtils.consume(response.getEntity());
            response.close();

            if (!downloadableURL.isPaused()) {
                System.out.println("download is completed, updating the download status");
                downloadableURL.setDownloadStatus(DownloadStatus.COMPLETED);
            }

            System.out.println("Download completed.");
        } catch (Exception e) {
            downloadableURL.setDownloadStatus(DownloadStatus.PAUSED);
            System.err.println("Error downloading file: " + e.getMessage());
        }
    }

    private String encodeURL(String url) {
        try {
            String decodedURL = URLDecoder.decode(url, "UTF-8");
            int indexOfFirstColon = decodedURL.indexOf(':');
            int indexOfSlashAfterColon = decodedURL.indexOf('/', indexOfFirstColon + 3);
            String schema = indexOfFirstColon > 0 ? decodedURL.substring(0, indexOfFirstColon) : null;
            String host = indexOfSlashAfterColon > 0 ? decodedURL.substring(indexOfFirstColon + 3, indexOfSlashAfterColon) : null;
            String[] remainingParts = decodedURL.substring(indexOfSlashAfterColon).split("\\?", 2);
            String path = remainingParts[0];
            String query = remainingParts[1];

            StringBuilder encodedPath = new StringBuilder();
            if (path != null) {
                String[] params = path.split("/");
                for (String param : params) {
                    String encodedValue = URLEncoder.encode(param, StandardCharsets.UTF_8);
                    encodedPath.append(encodedValue).append("/");
                }
                encodedPath.deleteCharAt(encodedPath.length() - 1);
            }

            StringBuilder encodedQuery = new StringBuilder();
            if (query != null) {
                String[] params = query.split("&");
                for (String param : params) {
                    String[] keyValue = param.split("=");
                    String encodedKey = URLEncoder.encode(keyValue[0], StandardCharsets.UTF_8);
                    String encodedValue = URLEncoder.encode(keyValue[1], StandardCharsets.UTF_8);
                    encodedQuery.append(encodedKey).append("=").append(encodedValue).append("&");
                }
                encodedQuery.deleteCharAt(encodedQuery.length() - 1);
            }

            return (schema + "://" + host + encodedPath + "?" + encodedQuery).replaceAll("\\+", "%20");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    private CloseableHttpResponse sendRequest(DownloadableURL downloadableURL) {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(5000)
                .setSocketTimeout(5000)
                .build();
        try {
            CloseableHttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(requestConfig).disableRedirectHandling().build();
            HttpGet httpGet = new HttpGet(downloadableURL.getUrl());
            long startByte = downloadableURL.getDownloadedSize();
            httpGet.setHeader("Range", "bytes=" + startByte + "-");
            CloseableHttpResponse response = httpClient.execute(httpGet);
            String redirectUri = checkRedirection(response);
            if (redirectUri != null) {
                System.out.println("Redirecting to location.");
                String encodedUrl = encodeURL(redirectUri);
                downloadableURL.setUrl(encodedUrl);
                return sendRequest(downloadableURL);
            } else {
                System.out.println("No redirection returning the response");
                return response;
            }
        } catch (Exception e) {
            System.out.println("error from send request: " + e.getMessage());
            downloadableURL.setDownloadStatus(DownloadStatus.PAUSED);
            System.out.println("download status: " + downloadableURL.getDownloadStatus());
            return null;
        }
    }

    private static String checkRedirection(CloseableHttpResponse response) {
        for (Header header : response.getAllHeaders()) {
            if (Objects.equals(header.getName(), "Location")) {
                return header.getValue();
            }
        }
        return null;
    }

    private String getFilenameWithExtension(CloseableHttpResponse response) {
        String filename = null;
        Header contentDispositionHeader = response.getFirstHeader("Content-Disposition");

        if (contentDispositionHeader != null && !contentDispositionHeader.getValue().equals("attachment")) {
            String contentDisposition = contentDispositionHeader.getValue();
            String[] parts = contentDisposition.split(";");
            for (String part : parts) {
                part = part.trim();
                if (part.startsWith("filename=")) {
                    return part.substring(part.indexOf('"') + 1, part.lastIndexOf('"'));
                }
            }
            System.out.println("filename: " + filename);
        }
        // If Content-Disposition not found, fallback to MIME type mapping.
        else {
            String contentType = response.getFirstHeader("Content-Type").getValue();
            TikaConfig config = TikaConfig.getDefaultConfig();
            try {
                MimeType mimeType = config.getMimeRepository().forName(contentType);
                String extension = mimeType.getExtension();
                filename = "File-".concat(UUID.randomUUID().toString().substring(0, 6) + extension);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        return filename;
    }

    private long getContentLength(CloseableHttpResponse response) {
        long contentLength = 0;
        HttpEntity httpEntity = response.getEntity();
        if (httpEntity != null) {
            contentLength = httpEntity.getContentLength();
        }
        return contentLength;
    }

}