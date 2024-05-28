package chanaka.downloader.stage.impl;

import chanaka.downloader.DownloaderApplication;
import chanaka.downloader.config.SpringFXMLLoader;
import chanaka.downloader.stage.BaseStage;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class MainStage implements BaseStage {

    @Setter
    Stage stage;

    @Autowired
    SpringFXMLLoader springFXMLLoader;

    @Override
    public void displayScene() throws IOException {
        springFXMLLoader.setLocation(DownloaderApplication.class.getResource("/fxml/main-view.fxml"));
        Scene scene = new Scene(springFXMLLoader.load(), 1000, 800);
        applyDefaultStyles(scene);
        stage.setTitle("Scheduled Downloader");
        stage.setScene(scene);
        stage.show();
    }


}
