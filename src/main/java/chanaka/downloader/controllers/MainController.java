package chanaka.downloader.controllers;

import chanaka.downloader.models.DownloadableUrlTable;
import chanaka.downloader.stage.impl.SchedulerStage;
import chanaka.downloader.services.DownloadManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import chanaka.downloader.models.DownloadableURL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MainController implements Initializable {
    @FXML
    private TextField urlInputField;

    @FXML
    private TableView<DownloadableURL> urlTableView;

    @FXML
    private CheckBox selectionCheck;

    @FXML
    private TextField saveLocationField;

    private static File saveDirectory;

    @Autowired
    SchedulerStage schedulerStage;

    @Autowired
    DownloadableUrlTable downloadableUrlTable;

    @Autowired
    DownloadManager downloadManager;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        downloadableUrlTable.setUrlTableView(urlTableView);

        //Set default file save location.
        saveDirectory = new File(System.getProperty("user.home"), "Downloads");
        setDefaultSaveLocation();
    }

    @FXML
    protected void addUrl() {
        String newUrl = urlInputField.getText();
        if(!newUrl.isEmpty()) {
            String url = urlInputField.getText();
            downloadableUrlTable.addUrl(url);
            urlInputField.clear();
        }
    }

    @FXML
    protected void downloadSelected() {
        if(!downloadableUrlTable.isDownloadableUrlTableEmpty()) {
           downloadManager.startSelectedDownloads();
        } else {
            System.out.println("Nothing to download");
        }
    }

    @FXML
    protected void browseSaveDirectory() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Download Location");

        directoryChooser.setInitialDirectory(saveDirectory);

        // Show directory chooser dialog
        File selectedSaveDirectory = directoryChooser.showDialog(urlInputField.getScene().getWindow());

        // Update save directory if a directory is selected
        if (selectedSaveDirectory != null) {
            saveDirectory = selectedSaveDirectory;
            saveLocationField.setText(saveDirectory.getAbsolutePath());
        }
    }

    @FXML
    protected void clearSelected() {
        downloadableUrlTable.clearSelected();
    }

    @FXML
    private void createScheduleDownloadStage() throws Exception{
        schedulerStage.displayScene();
    }

    @FXML
    public void selectionOnChange() {
       downloadableUrlTable.selectionOnChange(selectionCheck.isSelected());
    }

    public static String getSaveLocation() {
        return saveDirectory.getPath();
    }

    private void setDefaultSaveLocation() {
        saveLocationField.setText(saveDirectory.getAbsolutePath());
    }

}