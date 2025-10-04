package de.iu.project.iuipwa0201ghostnetfishing.web.controllers;

import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Models.GhostNetBusinessLayerModel;
import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Models.NetStatusBusinessLayerEnum;
import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Models.PersonBusinessLayerModel;
import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Services.IGhostNetBusinessLayerService;
import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Services.OperationResult;
import de.iu.project.iuipwa0201ghostnetfishing.web.Models.GhostNetForm;
import de.iu.project.iuipwa0201ghostnetfishing.web.Models.ReserveForm;
import de.iu.project.iuipwa0201ghostnetfishing.web.Models.RecoverForm;
import de.iu.project.iuipwa0201ghostnetfishing.web.Models.MissingForm;
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
        // Diagnostic log: confirm we loaded the ghost net for the reserve page
        GhostNetBusinessLayerModel g = opt.get();
        String assigned = g.getRecoveringPerson() != null ? g.getRecoveringPerson().getName() : null;
        log.info("GET reserve page: id={}, status={}, assigned={}", id, g.getStatus(), assigned);

        model.addAttribute("ghostNet", toViewModel(g));
        // Use a small dedicated form-object for reservation to avoid unrelated validation rules
        model.addAttribute("reserveForm", new ReserveForm());
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
        model.addAttribute("recoverForm", new RecoverForm());
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
                          @Valid @ModelAttribute("reserveForm") ReserveForm form,
                          BindingResult bindingResult,
                          RedirectAttributes ra,
                          Model model) {
        Optional<GhostNetBusinessLayerModel> opt = service.findById(id);

        if (opt.isEmpty()) {
            ra.addFlashAttribute("error", "Ghost net not found.");
            return "redirect:/ui/ghostnets";
        }

        if (bindingResult.hasErrors()) {
            // Re-render form with validation errors
            model.addAttribute("ghostNet", toViewModel(opt.get()));
            model.addAttribute("reserveForm", form);
            return "ghostnets/form-reserve";
        }

        // Build person model with trimmed name
        PersonBusinessLayerModel p = new PersonBusinessLayerModel();
        p.setName(form.getTrimmedPersonName());

        log.info("UI Reserve POST received: id={}, personName={}", id, form.getPersonName());

        OperationResult result = service.reserve(id, p);

        log.info("service.reserve returned: {} for id={}, personName={}", result, id, form.getPersonName());
        if (result == null) {
            // fallback: treat as error
            ra.addFlashAttribute("error", "Unable to reserve the net (internal)." );
            return "redirect:/ui/ghostnets";
        }

        switch (result) {
            case OK -> {
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
                var current = service.findById(id).orElse(null);
                String status = current != null && current.getStatus() != null ? current.getStatus().name() : "N/A";
                ra.addFlashAttribute("error", "This net can no longer be reserved (status: " + status + ")");
                if (current != null) {
                    return "redirect:/ui/ghostnets/" + current.getId();
                }
                return "redirect:/ui/ghostnets";
            }
            case BAD_REQUEST -> {
                // treat as validation-like error and re-render form
                model.addAttribute("ghostNet", toViewModel(opt.get()));
                bindingResult.reject("badRequest", "Invalid reservation request.");
                model.addAttribute("reserveForm", form);
                return "ghostnets/form-reserve";
            }
            default -> {
                ra.addFlashAttribute("error", "Unexpected result.");
                return "redirect:/ui/ghostnets";
            }
        }
    }

    @PostMapping("/{id}/recover")
    public String recover(@PathVariable("id") Long id,
                          @Valid @ModelAttribute("recoverForm") RecoverForm form,
                          BindingResult bindingResult,
                          RedirectAttributes ra,
                          Model model) {
        Optional<GhostNetBusinessLayerModel> opt = service.findById(id);

        if (opt.isEmpty()) {
            ra.addFlashAttribute("error", "Ghost net not found.");
            return "redirect:/ui/ghostnets";
        }

        if (bindingResult.hasErrors()) {
            // Re-render form with validation errors
            model.addAttribute("ghostNet", toViewModel(opt.get()));
            model.addAttribute("recoverForm", form);
            return "ghostnets/form-recover";
        }

        log.info("UI Recover POST received: id={}, notes={}", id, form.getNotes());

        OperationResult result = service.recover(id);

        log.info("service.recover returned: {} for id={}", result, id);
        if (result == null) {
            ra.addFlashAttribute("error", "Unable to mark net as recovered (internal).");
            return "redirect:/ui/ghostnets";
        }

        switch (result) {
            case OK -> {
                var updated = service.findById(id).orElse(null);
                ra.addFlashAttribute("ok", "Net marked as recovered successfully.");
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
                var current = service.findById(id).orElse(null);
                String status = current != null && current.getStatus() != null ? current.getStatus().name() : "N/A";
                ra.addFlashAttribute("error", "This net cannot be marked as recovered (status: " + status + ")");
                if (current != null) {
                    return "redirect:/ui/ghostnets/" + current.getId();
                }
                return "redirect:/ui/ghostnets";
            }
            case BAD_REQUEST -> {
                model.addAttribute("ghostNet", toViewModel(opt.get()));
                bindingResult.reject("badRequest", "Invalid recover request.");
                model.addAttribute("recoverForm", form);
                return "ghostnets/form-recover";
            }
            default -> {
                ra.addFlashAttribute("error", "Unexpected result.");
                return "redirect:/ui/ghostnets";
            }
        }
    }

    @GetMapping("/{id}/missing")
    public String missingForm(@PathVariable("id") Long id, Model model, RedirectAttributes ra) {
        Optional<GhostNetBusinessLayerModel> opt = service.findById(id);
        if (opt.isEmpty()) {
            ra.addFlashAttribute("error", "Ghost net nicht gefunden.");
            return "redirect:/ui/ghostnets";
        }
        model.addAttribute("ghostNet", toViewModel(opt.get()));
        model.addAttribute("missingForm", new MissingForm());
        return "ghostnets/form-missing";
    }

    @PostMapping("/{id}/missing")
    public String reportMissing(@PathVariable("id") Long id,
                          @Valid @ModelAttribute("missingForm") MissingForm form,
                          BindingResult bindingResult,
                          RedirectAttributes ra,
                          Model model) {
        Optional<GhostNetBusinessLayerModel> opt = service.findById(id);

        if (opt.isEmpty()) {
            ra.addFlashAttribute("error", "Ghost net not found.");
            return "redirect:/ui/ghostnets";
        }

        if (bindingResult.hasErrors()) {
            // Re-render form with validation errors
            model.addAttribute("ghostNet", toViewModel(opt.get()));
            model.addAttribute("missingForm", form);
            return "ghostnets/form-missing";
        }

        log.info("UI Missing POST received: id={}, notes={}", id, form.getNotes());

        OperationResult result = service.markMissing(id);

        log.info("service.markMissing returned: {} for id={}", result, id);
        if (result == null) {
            ra.addFlashAttribute("error", "Unable to mark net as missing (internal).");
            return "redirect:/ui/ghostnets";
        }

        switch (result) {
            case OK -> {
                var updated = service.findById(id).orElse(null);
                ra.addFlashAttribute("ok", "Net marked as missing successfully.");
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
                var current = service.findById(id).orElse(null);
                String status = current != null && current.getStatus() != null ? current.getStatus().name() : "N/A";
                ra.addFlashAttribute("error", "This net cannot be marked as missing (status: " + status + ")");
                if (current != null) {
                    return "redirect:/ui/ghostnets/" + current.getId();
                }
                return "redirect:/ui/ghostnets";
            }
            case BAD_REQUEST -> {
                model.addAttribute("ghostNet", toViewModel(opt.get()));
                bindingResult.reject("badRequest", "Invalid missing report.");
                model.addAttribute("missingForm", form);
                return "ghostnets/form-missing";
            }
            default -> {
                ra.addFlashAttribute("error", "Unexpected result.");
                return "redirect:/ui/ghostnets";
            }
        }
    }

}
