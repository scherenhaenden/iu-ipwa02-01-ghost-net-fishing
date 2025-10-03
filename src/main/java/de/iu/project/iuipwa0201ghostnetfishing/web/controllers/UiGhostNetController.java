package de.iu.project.iuipwa0201ghostnetfishing.web.controllers;

import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Models.GhostNetBusinessLayerModel;
import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Models.NetStatusBusinessLayerEnum;
import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Models.PersonBusinessLayerModel;
import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Services.IGhostNetBusinessLayerService;
import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Services.OperationResult;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/ui/ghostnets")
public class UiGhostNetController {

    private static final Logger log = LoggerFactory.getLogger(UiGhostNetController.class);

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
        log.debug("UI list requested with status param: '{}'", status);
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
        log.info("UI list: status='{}' -> returned {} ghostNets", status, ghostNets == null ? 0 : ghostNets.size());
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

    @PostMapping("/{id}/reserve")
    public String reserve(@PathVariable("id") Long id,
                          @Valid @ModelAttribute("ghostNetForm") GhostNetForm form,
                          BindingResult bindingResult,
                          RedirectAttributes ra,
                          Model model) {
        // If validation failed, redisplay the form with the current net info
        Optional<GhostNetBusinessLayerModel> opt = service.findById(id);
        if (bindingResult.hasErrors()) {
            model.addAttribute("ghostNet", toViewModel(opt.orElse(null)));
            model.addAttribute("ghostNetForm", form);
            return "ghostnets/form-reserve";
        }

        if (opt.isEmpty()) {
            ra.addFlashAttribute("error", "Ghost net not found.");
            return "redirect:/ui/ghostnets";
        }

        // Build person model
        PersonBusinessLayerModel p = null;
        if (form.getPersonName() == null || form.getPersonName().isBlank()) {
            // Validation: personName mandatory for reservation
            model.addAttribute("ghostNet", toViewModel(opt.get()));
            model.addAttribute("ghostNetForm", form);
            model.addAttribute("error", "Person name is required to reserve a net.");
            return "ghostnets/form-reserve";
        }
        if (form.getPersonName() != null && !form.getPersonName().isBlank()) {
            p = new PersonBusinessLayerModel();
            p.setName(form.getPersonName());
        }

        OperationResult result = service.reserve(id, p);
        if (result == null) {
            // fallback: treat as error
            ra.addFlashAttribute("error", "Unable to reserve the net (internal)." );
            return "redirect:/ui/ghostnets";
        }

        switch (result) {
            case OK -> {
                // fetch updated entity and redirect to detail so user sees the updated status
                var updated = service.findById(id).orElse(null);
                ra.addFlashAttribute("ok", "Net reserved successfully.");
                if (updated != null) {
                    return "redirect:/ui/ghostnets/" + updated.getId();
                }
                return "redirect:/ui/ghostnets";
            }
            case NOT_FOUND -> {
                ra.addFlashAttribute("error", "Ghost net not found.");
                return "redirect:/ui/ghostnets";
            }
            case CONFLICT -> {
                // Provide current status in message
                var current = service.findById(id).orElse(null);
                String status = current != null && current.getStatus() != null ? current.getStatus().name() : "N/A";
                ra.addFlashAttribute("error", "This net can no longer be reserved (status: " + status + ")");
                if (current != null) {
                    return "redirect:/ui/ghostnets/" + current.getId();
                }
                return "redirect:/ui/ghostnets";
            }
            case BAD_REQUEST -> {
                ra.addFlashAttribute("error", "Invalid reservation request. Provide a person name.");
                return "redirect:/ui/ghostnets/" + id + "/reserve";
            }
            default -> {
                ra.addFlashAttribute("error", "Unexpected result.");
                return "redirect:/ui/ghostnets";
            }
        }
    }

}
