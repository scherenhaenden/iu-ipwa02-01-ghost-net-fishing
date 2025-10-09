package de.iu.project.iuipwa0201ghostnetfishing.web.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {
    @GetMapping("/")
    public String home() {
        return "redirect:/ui/ghostnets";
    }



}

