package chanaka.downloader.services;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class Testclass {
    public Testclass() {
        System.out.println("crating the Testclass bean");
    }

    public void test() {
        System.out.println("testing...");
    }
}

