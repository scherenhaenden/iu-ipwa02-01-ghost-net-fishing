package de.iu.project.iuipwa0201ghostnetfishing.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    /*@Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Set high precedence for view controllers
        registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
    }*/
}
