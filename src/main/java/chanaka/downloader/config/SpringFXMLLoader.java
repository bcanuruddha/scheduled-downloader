package chanaka.downloader.config;

import javafx.fxml.FXMLLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Custom FXMLLoader that integrates with Spring Framework.
 * This class extends {@link FXMLLoader} and configures it to use Spring
 * for controller instantiation, allowing Spring and JavaFX to coexist
 * once the Spring application context has been bootstrapped.
 */
@Component
@Scope("prototype")
public class SpringFXMLLoader extends FXMLLoader {

    @Autowired
    public SpringFXMLLoader(ConfigurableApplicationContext context) {
        super();
        this.setControllerFactory(context::getBean);
    }
}
