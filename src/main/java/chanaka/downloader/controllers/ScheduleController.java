package chanaka.downloader.controllers;

import chanaka.downloader.services.Scheduler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.time.LocalDateTime;

@Controller
public class ScheduleController implements Initializable {

    @FXML
    private DatePicker datePicker;

    @FXML
    private TextField timeField;

    @Autowired
    Scheduler scheduler;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        datePicker.setValue(LocalDate.now());
        LocalTime currentTime = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        timeField.setText(currentTime.format(formatter));
    }

    public void startScheduler() {
        LocalDateTime dateTime = LocalDateTime.of(datePicker.getValue(), LocalTime.parse(timeField.getText(), DateTimeFormatter.ofPattern("HH:mm")));
        String dateTimeString = dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        scheduler.setScheduler(dateTimeString);
        scheduler.start();
    }
}