package de.iu.project.iuipwa0201ghostnetfishing.web.controllers;

import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Models.GhostNetBusinessLayerModel;
import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Models.NetStatusBusinessLayerEnum;
import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Models.PersonBusinessLayerModel;
import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Services.IGhostNetBusinessLayerService;
import de.iu.project.iuipwa0201ghostnetfishing.exceptions.ResourceNotFoundException;
import de.iu.project.iuipwa0201ghostnetfishing.web.Models.GhostNetForm;
import de.iu.project.iuipwa0201ghostnetfishing.web.Models.PersonWebLayerModel;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/ui/ghostnets")
public class UiGhostNetController {

    private final IGhostNetBusinessLayerService service;

    public UiGhostNetController(IGhostNetBusinessLayerService service) {

        this.service = service;
    }

    @GetMapping({"", "/"})
    public String list(@RequestParam(name = "status", required = false) String status, Model model) {
        List<GhostNetBusinessLayerModel> ghostNets;
        if (status == null || status.isBlank()) {
            ghostNets = service.findAll();
        } else {
            // convert to enum and call service (accept lower/upper case)
            try {
                NetStatusBusinessLayerEnum enumStatus = NetStatusBusinessLayerEnum.valueOf(status.trim().toUpperCase());
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
        GhostNetBusinessLayerModel ghostNet = service.findByIdOrThrow(id);
        model.addAttribute("ghostNet", ghostNet);
        model.addAttribute("ghostNetForm", new GhostNetForm());
        return "ghostnets/form-reserve";
    }

    @GetMapping("/{id}/recover")
    public String recoverForm(@PathVariable("id") Long id, Model model) {
        GhostNetBusinessLayerModel ghostNet = service.findByIdOrThrow(id);
        model.addAttribute("ghostNet", ghostNet);
        model.addAttribute("ghostNetForm", new GhostNetForm());
        return "ghostnets/form-recover";
    }


    @PostMapping
    public String create(@ModelAttribute("ghostNetForm") GhostNetForm form,
                         RedirectAttributes ra) {
        if (form.getLocation() == null || form.getLocation().isBlank()) {
            ra.addFlashAttribute("error", "Location ist erforderlich.");
            return "redirect:/ui/ghostnets/new";
        }

        GhostNetBusinessLayerModel b = new GhostNetBusinessLayerModel();
        b.setLocation(form.getLocation());
        b.setSize(form.getSize());
        b.setStatus(NetStatusBusinessLayerEnum.REPORTED); // siempre REPORTED

        if (form.getPersonName() != null && !form.getPersonName().isBlank()) {
            PersonBusinessLayerModel p = new PersonBusinessLayerModel();
            p.setName(form.getPersonName());
            b.setRecoveringPerson(p); // si vacío => anónimo (null)
        }

        service.save(b);
        ra.addFlashAttribute("ok", "Geisternetz erfasst.");
        return "redirect:/ui/ghostnets";
    }

    // TODO: Implement POST endpoints for create/reserve/recover when API is ready
    // @PostMapping
    // public String create(...) { }
}
