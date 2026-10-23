package de.iu.project.iuipwa0201ghostnetfishing.web.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class SimpleTestController {

    @GetMapping("/illegal-arg")
    /**
     * Throws an IllegalArgumentException with a message indicating an invalid parameter.
     */
    public void illegalArg() {
        throw new IllegalArgumentException("invalid param");
    }

    @GetMapping("/illegal-state")
    /**
     * Throws an IllegalStateException indicating that an entity already exists.
     */
    public void illegalState() {
        throw new IllegalStateException("already exists");
    }
}

