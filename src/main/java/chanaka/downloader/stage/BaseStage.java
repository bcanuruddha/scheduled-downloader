package chanaka.downloader.stage;

import chanaka.downloader.stage.impl.MainStage;
import javafx.scene.Scene;

import java.io.IOException;
import java.util.Objects;

public interface BaseStage {
    void displayScene() throws IOException;

    default void applyDefaultStyles(Scene scene) {
        scene.getStylesheets().add(Objects.requireNonNull(MainStage.class.getResource("/styles/main-styles.css")).toExternalForm());
    }

}
