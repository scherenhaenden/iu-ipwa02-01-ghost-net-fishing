package de.iu.project.iuipwa0201ghostnetfishing.web.controllers;

import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Models.GhostNetBusinessLayerModel;
import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Models.NetStatusBusinessLayerEnum;
import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Models.PersonBusinessLayerModel;
import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Services.IGhostNetBusinessLayerService;
import de.iu.project.iuipwa0201ghostnetfishing.web.Models.GhostNetForm;
import de.iu.project.iuipwa0201ghostnetfishing.web.Models.GhostNetViewModel;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/ui/ghostnets")
public class UiGhostNetController {

    private final IGhostNetBusinessLayerService service;

    public UiGhostNetController(IGhostNetBusinessLayerService service) {
        this.service = service;
    }

    private GhostNetViewModel toViewModel(GhostNetBusinessLayerModel b) {
        if (b == null) return null;
        String personName = (b.getRecoveringPerson() != null) ? b.getRecoveringPerson().getName() : null;
        return new GhostNetViewModel(b.getId(), b.getLocation(), b.getSize(), b.getStatus(), b.getCreatedAt(), personName);
    }

    @GetMapping({"", "/"})
    public String list(@RequestParam(name = "status", required = false) String status, Model model) {
        List<GhostNetViewModel> ghostNets;
        if (status == null || status.isBlank()) {
            ghostNets = service.findAll().stream().map(this::toViewModel).collect(Collectors.toList());
        } else {
            try {
                NetStatusBusinessLayerEnum enumStatus = NetStatusBusinessLayerEnum.valueOf(status.trim().toUpperCase());
                ghostNets = service.findByStatus(enumStatus).stream().map(this::toViewModel).collect(Collectors.toList());
            } catch (IllegalArgumentException ex) {
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
    public String reserveForm(@PathVariable("id") Long id, Model model, RedirectAttributes ra) {
        Optional<GhostNetBusinessLayerModel> opt = service.findById(id);
        if (opt.isEmpty()) {
            ra.addFlashAttribute("error", "Ghost net nicht gefunden.");
            return "redirect:/ui/ghostnets";
        }
        model.addAttribute("ghostNet", toViewModel(opt.get()));
        model.addAttribute("ghostNetForm", new GhostNetForm());
        return "ghostnets/form-reserve";
    }

    @GetMapping("/{id}/recover")
    public String recoverForm(@PathVariable("id") Long id, Model model, RedirectAttributes ra) {
        Optional<GhostNetBusinessLayerModel> opt = service.findById(id);
        if (opt.isEmpty()) {
            ra.addFlashAttribute("error", "Ghost net nicht gefunden.");
            return "redirect:/ui/ghostnets";
        }
        model.addAttribute("ghostNet", toViewModel(opt.get()));
        model.addAttribute("ghostNetForm", new GhostNetForm());
        return "ghostnets/form-recover";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("ghostNetForm") GhostNetForm form,
                         BindingResult bindingResult,
                         RedirectAttributes ra,
                         Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("ghostNetForm", form);
            return "ghostnets/form-create";
        }

        GhostNetBusinessLayerModel b = new GhostNetBusinessLayerModel();
        b.setLocation(form.getLocation());
        b.setSize(form.getSize());
        b.setStatus(NetStatusBusinessLayerEnum.REPORTED);

        if (form.getPersonName() != null && !form.getPersonName().isBlank()) {
            PersonBusinessLayerModel p = new PersonBusinessLayerModel();
            p.setName(form.getPersonName());
            b.setRecoveringPerson(p);
        }

        service.save(b);
        ra.addFlashAttribute("ok", "Geisternetz erfasst.");
        return "redirect:/ui/ghostnets";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable("id") Long id, Model model, RedirectAttributes ra) {
        Optional<GhostNetBusinessLayerModel> opt = service.findById(id);
        if (opt.isEmpty()) {
            ra.addFlashAttribute("error", "Ghost net nicht gefunden.");
            return "redirect:/ui/ghostnets";
        }
        model.addAttribute("ghostNet", toViewModel(opt.get()));
        return "ghostnets/detail";
    }

    // TODO: Implement POST endpoints for create/reserve/recover when API is ready
}
