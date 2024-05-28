package chanaka.downloader.services;

import chanaka.downloader.controllers.MainController;
import chanaka.downloader.models.DownloadableURL;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
public class FileService {

    public void openSavedFileLocation(DownloadableURL downloadableURL) {
        try {
            String saveFilePath = MainController.getSaveLocation();
            File downloadedFile = new File(saveFilePath, downloadableURL.getFileName());
            if (downloadedFile.exists()) {
                openFileInDirectory(downloadedFile);
            } else {
                System.err.println("Downloaded file not found.");
            }
        } catch (IOException ex) {
            System.err.println("Error opening file: " + ex.getMessage());
        }
    }

    private void openFileInDirectory(File file) throws IOException {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("linux")) {
            if (new File("/usr/bin/nautilus").exists()) {
                new ProcessBuilder("nautilus", file.getAbsolutePath()).start();
            } else {
                new ProcessBuilder("xdg-open", file.getParent()).start();
            }
        } else if (osName.contains("mac")) {
            new ProcessBuilder("open", "-R", file.getAbsolutePath()).start();
        } else if (osName.contains("win")) {
            new ProcessBuilder("explorer.exe", "/select,", file.getAbsolutePath()).start();
        } else {
            throw new UnsupportedOperationException("Unsupported operating system: " + osName);
        }
    }

}
