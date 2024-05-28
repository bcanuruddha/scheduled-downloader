package chanaka.downloader.factory;

import chanaka.downloader.models.DownloadableURL;
import chanaka.downloader.services.GUIManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class DownloadableUrlFactory {

    private static final Logger logger = LoggerFactory.getLogger(DownloadableUrlFactory.class);

    private final ConfigurableApplicationContext context;
    private final GUIManager guiManager;

    @Autowired
    public DownloadableUrlFactory(ConfigurableApplicationContext context, GUIManager guiManager) {
        this.context = context;
        this.guiManager = guiManager;
    }

    public DownloadableURL createDownloadableUrl(String url) {
        logger.debug("Calling createDownloadableUrl factory method");
        return context.getBean(DownloadableURL.class, url, guiManager);
    }
}
