package chanaka.downloader.factory;

import chanaka.downloader.models.DownloadClient;
import chanaka.downloader.models.DownloadableURL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class DownloadClientFactory {
    private final ConfigurableApplicationContext context;

    @Autowired
    public DownloadClientFactory(ConfigurableApplicationContext context) {
        this.context = context;
    }

    public DownloadClient createDownloadClient(DownloadableURL downloadableURL) {
        return context.getBean(DownloadClient.class, downloadableURL);
    }
}
