package chanaka.downloader.services;

import chanaka.downloader.factory.DownloadServiceFactory;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class Scheduler {

    @Autowired
    DownloadManager downloadManager;

    private String scheduledDateTime;

    public void setScheduler(String dateTimeString) {
        this.scheduledDateTime = dateTimeString;
    }
    public void start() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = dateFormat.parse(scheduledDateTime);
            long delay = date.getTime() - System.currentTimeMillis();
            scheduler.schedule(downloadManager, delay, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            System.err.println("Error parsing date: " + e.getMessage());
            e.printStackTrace();
        }
        scheduler.shutdown();
    }
}
