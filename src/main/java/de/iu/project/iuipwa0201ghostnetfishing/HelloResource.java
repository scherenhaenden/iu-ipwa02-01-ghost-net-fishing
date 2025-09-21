package de.iu.project.iuipwa0201ghostnetfishing;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HelloResource {
    @GetMapping("/hello-world")
    /**
     * Returns a greeting message.
     */
    public String hello() {
        return "Hello, World!";
    }
}