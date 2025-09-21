package de.iu.project.iuipwa0201ghostnetfishing;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/* indo docs: HelloResource class
   Simple REST controller for a basic "Hello World" API endpoint.
   Demonstrates Spring Web MVC for JSON responses without view rendering.
   Mapped to /api base path.
*/
@RestController
@RequestMapping("/api")
public class HelloResource {

    /* indo docs: Hello World endpoint
       GET request handler returning a plain string "Hello, World!".
       Useful for testing if the application is running and API is accessible.
    */
    @GetMapping("/hello-world")
    public String hello() {
        return "Hello, World!";
    }
}