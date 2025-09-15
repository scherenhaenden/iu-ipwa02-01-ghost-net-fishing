package de.iu.project.iuipwa0201ghostnetfishing.web;

import de.iu.project.iuipwa0201ghostnetfishing.model.GhostNet;
import de.iu.project.iuipwa0201ghostnetfishing.service.GhostNetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/")
public class GhostNetController {

    @Autowired
    private GhostNetService service;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("techStack", new String[]{
            "Spring Boot 3.3", "Java 21", "Hibernate JPA", "Thymeleaf", "H2 Database"
        });
        return "index";
    }

    @GetMapping("/welcome")
    public String welcome(Model model) {
        model.addAttribute("ghostNets", service.findAll());
        model.addAttribute("name", "");
        return "welcome";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute("name") String name, Model model) {
        if (name != null && !name.trim().isEmpty()) {
            service.create(name.trim());
        }
        model.addAttribute("ghostNets", service.findAll());
        model.addAttribute("name", "");
        return "welcome";
    }

}

