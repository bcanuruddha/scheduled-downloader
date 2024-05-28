package chanaka.downloader.services;

import chanaka.downloader.factory.DownloadClientFactory;
import chanaka.downloader.models.DownloadClient;
import chanaka.downloader.models.DownloadableURL;
import chanaka.downloader.util.DownloadStatus;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class DownloadService extends Service<Void> {

    private final DownloadableURL downloadableUrl;

    @Autowired
    DownloadClientFactory downloadClientFactory;

    public DownloadService(DownloadableURL downloadableUrl) {
        this.downloadableUrl = downloadableUrl;
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() {
                DownloadClient downloadClient = downloadClientFactory.createDownloadClient(downloadableUrl);
                if (downloadableUrl.isWaiting() && downloadableUrl.isSelected()) {
                    downloadableUrl.setDownloadStatus(DownloadStatus.DOWNLOADING);
                    downloadClient.startDownload();
                } else if (downloadableUrl.isPaused()) {
                    downloadableUrl.setDownloadStatus(DownloadStatus.DOWNLOADING);
                    downloadClient.resumeDownload();
                }
                return null;
            }

        };
    }
}
