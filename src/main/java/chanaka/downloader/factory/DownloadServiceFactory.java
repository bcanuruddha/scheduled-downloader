package chanaka.downloader.factory;

import chanaka.downloader.models.DownloadableURL;
import chanaka.downloader.services.DownloadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class DownloadServiceFactory {

    private final ConfigurableApplicationContext context;

    @Autowired
    public DownloadServiceFactory(ConfigurableApplicationContext context) {
        this.context = context;
    }

    public DownloadService createDownloadService(DownloadableURL downloadableURL) {
        return context.getBean(DownloadService.class, downloadableURL);
    }

}
