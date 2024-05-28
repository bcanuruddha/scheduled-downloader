package chanaka.downloader.models;

import chanaka.downloader.factory.DownloadableUrlFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Getter
@Component
public class DownloadableUrlTable {

    @FXML
    private TableView<DownloadableURL> urlTableView;

    @Autowired
    DownloadableUrlFactory downloadableUrlFactory;

    private final ObservableList<DownloadableURL> downloadableUrlList = FXCollections.observableArrayList();

    public void setUrlTableView(TableView<DownloadableURL> urlTableView) {
        this.urlTableView = urlTableView;
        urlTableView.setItems(downloadableUrlList);
        initTableItemOnStatusChange();
    }

    public void addUrl(String url) {
        DownloadableURL  downloadableURL = downloadableUrlFactory.createDownloadableUrl(url);
        downloadableUrlList.add(downloadableURL);
    }

    public boolean isDownloadableUrlTableEmpty() {
        return downloadableUrlList.isEmpty();
    }

    public void clearSelected() {
        downloadableUrlList.removeIf(DownloadableURL::isSelected);
        urlTableView.refresh();
    }

    public void selectionOnChange(boolean isSelected) {
        for (DownloadableURL downloadableUrl : downloadableUrlList) {
            downloadableUrl.setSelection(isSelected);
        }
    }

    private void initTableItemOnStatusChange() {
        urlTableView.setRowFactory(tv -> new TableRow<DownloadableURL>() {
            @Override
            protected void updateItem(DownloadableURL downloadableURL, boolean empty) {
                super.updateItem(downloadableURL, empty);
                if (downloadableURL == null || empty) {
                    return;
                }
                downloadableURL.applyRowStylesOnStatusChange(this);
            }
        });
    }

}
