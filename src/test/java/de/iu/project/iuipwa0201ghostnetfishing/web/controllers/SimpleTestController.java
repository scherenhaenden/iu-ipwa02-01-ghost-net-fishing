package de.iu.project.iuipwa0201ghostnetfishing.web.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class SimpleTestController {

    @GetMapping("/illegal-arg")
    public void illegalArg() {
        throw new IllegalArgumentException("invalid param");
    }

    @GetMapping("/illegal-state")
    public void illegalState() {
        throw new IllegalStateException("already exists");
    }
}

