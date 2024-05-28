package chanaka.downloader.services;

import chanaka.downloader.factory.DownloadServiceFactory;
import chanaka.downloader.models.DownloadableURL;
import chanaka.downloader.models.DownloadableUrlTable;
import javafx.collections.ObservableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DownloadManager implements Runnable {

    @Autowired
    DownloadableUrlTable downloadableUrlTable;

    @Autowired
    DownloadServiceFactory downloadServiceFactory;

    @Override
    public void run() {
        startSelectedDownloads();
    }

    public void startSelectedDownloads() {
        ObservableList<DownloadableURL> downloadableUrlList = downloadableUrlTable.getDownloadableUrlList();
        for (DownloadableURL downloadableUrl : downloadableUrlList) {
            System.out.println(downloadableUrl.getUrl());
            downloadUrl(downloadableUrl);
        }
    }

    public void downloadUrl(DownloadableURL downloadableUrl) {
        downloadServiceFactory.createDownloadService(downloadableUrl).start();
    }

}
