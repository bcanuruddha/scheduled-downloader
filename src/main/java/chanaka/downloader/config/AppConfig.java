package chanaka.downloader.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("chanaka.downloader")
public class AppConfig {

//    Main scene will only create after Spring context bootstrap.
//    @Bean
//    @Lazy(value = true)
//    public MainStage mainScene(Stage stage) {
//        return new MainStage(stage);
//    }

}
