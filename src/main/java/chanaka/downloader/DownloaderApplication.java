package chanaka.downloader;

import chanaka.downloader.config.AppConfig;
import chanaka.downloader.stage.impl.MainStage;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@SpringBootApplication
public class DownloaderApplication extends Application {

    private ConfigurableApplicationContext context;

    @Override
    public void init() {
        context = new AnnotationConfigApplicationContext(AppConfig.class);
    }

    @Override
    public void start(Stage stage) throws Exception {
        MainStage mainStage = context.getBean(MainStage.class);
        mainStage.setStage(stage);
        mainStage.displayScene();
    }

    @Override
    public void stop() {
        context.close();
        Platform.exit();
    }

    public static void main(String[] args) {
        launch();
    }
}
