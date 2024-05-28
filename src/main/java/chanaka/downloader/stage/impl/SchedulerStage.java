package chanaka.downloader.stage.impl;

import chanaka.downloader.DownloaderApplication;
import chanaka.downloader.config.SpringFXMLLoader;
import chanaka.downloader.stage.BaseStage;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Component
public class SchedulerStage implements BaseStage {
    Stage ScheduleDownloadStage;

    @Autowired
    SpringFXMLLoader springFXMLLoader;

    @Override
    public void displayScene() throws IOException {
        if (ScheduleDownloadStage == null) {
            ScheduleDownloadStage = new Stage();
            springFXMLLoader.setLocation(DownloaderApplication.class.getResource("/fxml/schedule-view.fxml"));
            Scene scene = new Scene(springFXMLLoader.load(), 300, 200);
            ScheduleDownloadStage.setTitle("Schedule Downloader");
            ScheduleDownloadStage.setScene(scene);
            ScheduleDownloadStage.show();
            applyDefaultStyles(scene);
        } else {
            ScheduleDownloadStage.requestFocus();
        }

        // Reset the stage reference when it's closed
        ScheduleDownloadStage.setOnCloseRequest(event -> {
            ScheduleDownloadStage = null;
        });
    }

    public void close() {
        ScheduleDownloadStage.close();
    }
}
