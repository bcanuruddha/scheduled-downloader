package chanaka.downloader.services;

import chanaka.downloader.factory.DownloadServiceFactory;
import chanaka.downloader.models.DownloadableURL;
import chanaka.downloader.models.DownloadableUrlTable;
import chanaka.downloader.util.DownloadStatus;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.TableRow;
import javafx.scene.image.ImageView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Timer;
import java.util.TimerTask;

@Component
@Scope("prototype")
public class GUIManager {

    private long prevSecondDownload = 0;
    private long prevSecond = System.currentTimeMillis();
    private double currentDownloadSpeed = 0;

    @Autowired
    DownloadServiceFactory downloadServiceFactory;

    @Autowired
    private FileService fileService;

    @Autowired
    private DownloadableUrlTable downloadableUrlTable;

    public void startRowViewUpdateTimer(DownloadableURL downloadableURL) {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (downloadableURL.getDownloadedSize() < downloadableURL.getFileSize() && downloadableURL.isDownloading()) {
                    Platform.runLater(() -> {
                        updateSpeedColumn(downloadableURL);
                        updateTimeRemainingColumn(downloadableURL);
                    });
                } else {
                    Platform.runLater(() -> {
                        timer.cancel();
                        downloadableURL.setSpeed("0.0 MB/s");
                        if(downloadableURL.isPaused()) {
                            downloadableURL.setTimeRemaining("Download Paused");
                        }
                        if(downloadableURL.isCompleted()) {
                            downloadableURL.setTimeRemaining("Download Complete");
                        }

                        // we have to manually refresh the table because worker thread is stopped at this point.
                        downloadableUrlTable.getUrlTableView().refresh();
                    });
                }
            }
        }, 0, 1000);

    }

    private void updateSpeedColumn(DownloadableURL downloadableURL) {
        long nowSecond = System.currentTimeMillis();
        long timeDifference = (nowSecond - prevSecond) / 1000; // Time difference in seconds
        if (timeDifference > 0) {
            currentDownloadSpeed = ((downloadableURL.getDownloadedSize() - prevSecondDownload) / (double) (timeDifference * 1024 * 1024));
            String speedString = String.format("%.3f MB/s", currentDownloadSpeed);
            downloadableURL.setSpeed(speedString);
            downloadableUrlTable.getUrlTableView().refresh();
        }
        prevSecondDownload = downloadableURL.getDownloadedSize();
        prevSecond = nowSecond;
    }

    private void updateTimeRemainingColumn(DownloadableURL downloadableURL) {
        if (currentDownloadSpeed > 0) {
            double timeRemainingSeconds = ((downloadableURL.getFileSize() - downloadableURL.getDownloadedSize()) / (currentDownloadSpeed * 1024 * 1024));
            String timeUnit;
            if (timeRemainingSeconds > 86400) {
                timeUnit = "days";
                timeRemainingSeconds = timeRemainingSeconds / 86400;
            } else if (timeRemainingSeconds > 3600) {
                timeUnit = "hours";
                timeRemainingSeconds = timeRemainingSeconds / 3600;
            } else if (timeRemainingSeconds > 60) {
                timeUnit = "minutes";
                timeRemainingSeconds = timeRemainingSeconds / 60;
            } else {
                timeUnit = "seconds";
            }
            int timeRemaining = (int) Math.round(timeRemainingSeconds);
            String timeRemainingString = String.format("%d %s", timeRemaining, timeUnit);
            downloadableURL.setTimeRemaining(timeRemainingString);
        } else {
            downloadableURL.setTimeRemaining("Calculating...");
        }
    }

    public void applyRowStylesOnStatusChange(TableRow<DownloadableURL> row, DownloadableURL item) {
        switch (item.getDownloadStatus()) {
            case WAITING:
                setStartIcon(item);
                row.setStyle("");
                row.getStyleClass().setAll("waiting-row");
                break;
            case DOWNLOADING:
                setPauseIcon(item);
                row.getStyleClass().setAll("downloading-row");
                break;
            case PAUSED:
                setStartIcon(item);
                row.getStyleClass().setAll("paused-row");
                break;
            case COMPLETED:
                setCompletedIcon(item);
                row.getStyleClass().setAll("completed-row");
                break;
            default:
                break;
        }
    }

    public void setActionButton(DownloadableURL downloadableURL) {
        Button action = downloadableURL.getAction();
        action.getStyleClass().add("action-button");
        setStartIcon(downloadableURL);

        action.setOnAction(event -> actionButtonEventHandler(downloadableURL));
    }

    public void actionButtonEventHandler(DownloadableURL downloadableURL) {
        DownloadStatus downloadStatus = downloadableURL.getDownloadStatus();
        switch (downloadStatus) {
            case WAITING, PAUSED:
                setPauseIcon(downloadableURL);
                downloadServiceFactory.createDownloadService(downloadableURL).start();
                break;
            case DOWNLOADING:
                setStartIcon(downloadableURL);
                downloadableURL.setDownloadStatus(DownloadStatus.PAUSED);
                break;
            case COMPLETED:
                fileService.openSavedFileLocation(downloadableURL);
                break;
            default:
                setStartIcon(downloadableURL);
                break;
        }
    }

    private void setStartIcon(DownloadableURL downloadableURL) {
        ImageView startIcon = new ImageView("/images/start-download.png");
        downloadableURL.getAction().setGraphic(startIcon);
        setIconSize(startIcon);
    }

    private void setPauseIcon(DownloadableURL downloadableURL) {
        ImageView pauseIcon = new ImageView("/images/pause-download.png");
        downloadableURL.getAction().setGraphic(pauseIcon);
        setIconSize(pauseIcon);
    }

    private void setCompletedIcon(DownloadableURL downloadableURL) {
        ImageView completedIcon = new ImageView("/images/completed-download.png");
        downloadableURL.getAction().setGraphic(completedIcon);
        setIconSize(completedIcon);
    }

    private void setIconSize(ImageView icon) {
        icon.setFitWidth(15);
        icon.setFitHeight(15);
    }

}

