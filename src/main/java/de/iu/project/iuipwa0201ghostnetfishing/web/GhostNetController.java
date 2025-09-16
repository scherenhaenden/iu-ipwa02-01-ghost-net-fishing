package de.iu.project.iuipwa0201ghostnetfishing.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/* indo docs: GhostNetController class
   Spring MVC controller handling web requests for GhostNet-related pages.
   Uses Thymeleaf for view rendering and GhostNetService for business logic.
   Maps to root path ("/") and provides index, welcome, and create endpoints.
*/
@Controller
@RequestMapping("/")
public class GhostNetController {

    /* indo docs: Injected service dependency
       Autowired GhostNetService for accessing GhostNet data and operations.
    */
    @Autowired
    private GhostNetService service;

    /* indo docs: Root endpoint - Index page
       Renders the main index page with tech stack information.
       Returns "index" Thymeleaf template.
    */
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("techStack", new String[]{
            "Spring Boot 3.3", "Java 21", "Hibernate JPA", "Thymeleaf", "H2 Database"
        });
        return "index";
    }

    /* indo docs: Welcome endpoint - List GhostNets
       Displays the welcome page with a list of all GhostNets (newest first).
       Initializes empty name field for form binding.
    */
    @GetMapping("/welcome")
    public String welcome(Model model) {
        model.addAttribute("ghostNets", service.findAll());
        model.addAttribute("name", "");
        return "welcome";
    }

    /* indo docs: Create endpoint - Add new GhostNet
       Handles POST request to create a new GhostNet from form input.
       Validates name, saves via service, and redirects to welcome with updated list.
    */
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
