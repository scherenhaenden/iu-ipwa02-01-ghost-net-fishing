package de.iu.project.iuipwa0201ghostnetfishing.web.controllers;

import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Models.GhostNetBusinessLayerModel;
import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Models.NetStatusBusinessLayerEnum;
import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Services.IGhostNetBusinessLayerService;
import de.iu.project.iuipwa0201ghostnetfishing.web.Models.GhostNetForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/ui/ghostnets")
public class UiGhostNetController {

    private final IGhostNetBusinessLayerService service;

    public UiGhostNetController(IGhostNetBusinessLayerService service) {
        this.service = service;
    }

    @GetMapping
    public String list(@RequestParam(name = "status", required = false) String status, Model model) {
        List<GhostNetBusinessLayerModel> ghostNets;
        if (status == null || status.isBlank()) {
            ghostNets = service.findAll();
        } else {
            // convert to enum and call service
            try {
                NetStatusBusinessLayerEnum enumStatus = NetStatusBusinessLayerEnum.valueOf(status.trim());
                ghostNets = service.findByStatus(enumStatus);
            } catch (IllegalArgumentException ex) {
                // invalid status value -> empty list
                ghostNets = Collections.emptyList();
            }
        }
        model.addAttribute("ghostNets", ghostNets);
        model.addAttribute("selectedStatus", status == null ? "" : status);
        return "ghostnets/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("ghostNetForm", new GhostNetForm());
        return "ghostnets/form-create";
    }

    @GetMapping("/{id}/reserve")
    public String reserveForm(@PathVariable("id") Long id, Model model) {
        // For presentation: fetch the ghostnet to show details if present
        GhostNetBusinessLayerModel ghostNet = null;
        try {
            List<GhostNetBusinessLayerModel> all = service.findAll();
            ghostNet = all.stream().filter(g -> id != null && id.equals(g.getId())).findFirst().orElse(null);
        } catch (Exception ignored) {}
        model.addAttribute("ghostNet", ghostNet);
        model.addAttribute("ghostNetForm", new GhostNetForm());
        return "ghostnets/form-reserve";
    }

    @GetMapping("/{id}/recover")
    public String recoverForm(@PathVariable("id") Long id, Model model) {
        GhostNetBusinessLayerModel ghostNet = null;
        try {
            List<GhostNetBusinessLayerModel> all = service.findAll();
            ghostNet = all.stream().filter(g -> id != null && id.equals(g.getId())).findFirst().orElse(null);
        } catch (Exception ignored) {}
        model.addAttribute("ghostNet", ghostNet);
        model.addAttribute("ghostNetForm", new GhostNetForm());
        return "ghostnets/form-recover";
    }

    // TODO: Implement POST endpoints for create/reserve/recover when API is ready
    // @PostMapping
    // public String create(...) { }
}
