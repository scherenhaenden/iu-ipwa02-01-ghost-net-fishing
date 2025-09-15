package de.iu.project.iuipwa0201ghostnetfishing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/* indo docs: GhostNetFishingApplication class
   Main entry point for the Spring Boot application.
   Enables auto-configuration, component scanning, and starts the embedded Tomcat server.
   Run with main() method to bootstrap the entire application context.
*/
@SpringBootApplication
public class GhostNetFishingApplication {

    /* indo docs: Application bootstrap method
       Initializes and runs the Spring Boot application with command-line arguments.
       Configures the context, starts the web server on port 8080 by default.
    */
    public static void main(String[] args) {
        SpringApplication.run(GhostNetFishingApplication.class, args);
    }
}
