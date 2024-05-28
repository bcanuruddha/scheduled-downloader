package chanaka.downloader.models;

import chanaka.downloader.services.GUIManager;
import chanaka.downloader.util.DownloadStatus;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableRow;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@Scope("prototype")
public class DownloadableURL{

    private final SimpleStringProperty fileName = new SimpleStringProperty();
    private final SimpleStringProperty speed = new SimpleStringProperty("0.0 MB/s");
    private final SimpleStringProperty timeRemaining = new SimpleStringProperty();
    private final SimpleStringProperty url = new SimpleStringProperty();
    private final CheckBox selection = new CheckBox();
    private final ProgressBar progressBar = new ProgressBar(0);

    private GUIManager guiManager;
    private String saveFilePath;
    private long downloadedSize = 0;
    private long fileSize = 0;
    private Button action = new Button();
    private DownloadStatus downloadStatus = DownloadStatus.WAITING;

    @Autowired
    public DownloadableURL(String url, GUIManager guiManager) {
        this.guiManager = guiManager;
        this.url.setValue(url);
        selection.setSelected(true);
        guiManager.setActionButton(this);
    }

    public boolean isSelected() {
        return selection.isSelected();
    }

    public void setSelection(boolean selected) {
        this.selection.setSelected(selected);
    }

    public String getFileName() {
        return fileName.getValue();
    }

    public void setFileName(String fileType) {
        this.fileName.setValue(fileType);
    }

    public String getSpeed() {
        return speed.getValue();
    }

    public void setSpeed(String speed) {
        this.speed.setValue(speed);
    }

    public String getTimeRemaining() {
        return timeRemaining.getValue();
    }

    public void setTimeRemaining(String timeRemaining) {
        this.timeRemaining.setValue(timeRemaining);
    }

    public String getUrl() {
        return url.getValue();
    }

    public void setUrl(String url) {
        this.url.setValue(url);
    }

    public void updateDownloadedSize(long newDownloadSize) {
        downloadedSize += newDownloadSize;
    }

    public void setProgressBar() {
        double progress = (double) downloadedSize / fileSize;
        progressBar.setProgress(progress);
    }

    public boolean isDownloading() {
        return downloadStatus.equals(DownloadStatus.DOWNLOADING);
    }

    public boolean isWaiting() {
        return downloadStatus.equals(DownloadStatus.WAITING);
    }

    public boolean isPaused() {
        return downloadStatus.equals(DownloadStatus.PAUSED);
    }

    public boolean isCompleted() {
        return downloadStatus.equals(DownloadStatus.COMPLETED);
    }

    public void updateView() {
        guiManager.startRowViewUpdateTimer(this);
    }

    public void applyRowStylesOnStatusChange(TableRow<DownloadableURL> row){
        guiManager.applyRowStylesOnStatusChange(row, this);
    }
}
