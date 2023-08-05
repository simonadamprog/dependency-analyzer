package hu.web220.dependency.analyzer.test.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        try (final ConfigurableApplicationContext application = SpringApplication.run(Application.class, args)) {
            System.out.println(application.getApplicationName() + " Started.");
        }
        catch (Throwable t) {
            System.out.println("Some exception happened during runtime: " + t.getMessage());
            t.printStackTrace();
        }
    }
}
