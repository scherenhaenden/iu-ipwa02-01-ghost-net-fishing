package de.iu.project.iuipwa0201ghostnetfishing.web.controllers;

import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Models.GhostNetBusinessLayerModel;
import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Models.NetStatusBusinessLayerEnum;
import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Models.PersonBusinessLayerModel;
import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Services.IGhostNetBusinessLayerService;
import de.iu.project.iuipwa0201ghostnetfishing.web.Mappers.GhostNetWebLayerMapper;
import de.iu.project.iuipwa0201ghostnetfishing.web.Mappers.GhostNetWebToBusinessMapper;
import de.iu.project.iuipwa0201ghostnetfishing.web.Mappers.PersonWebToBusinessMapper;
import de.iu.project.iuipwa0201ghostnetfishing.web.Models.CreateGhostNetRequest;
import de.iu.project.iuipwa0201ghostnetfishing.web.Models.GhostNetWebLayerModel;
import de.iu.project.iuipwa0201ghostnetfishing.web.Models.RecoverRequest;
import de.iu.project.iuipwa0201ghostnetfishing.web.Models.ReserveRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * REST API for GhostNet resources.
 */
@RestController
@RequestMapping("/api/ghostnets")
@Validated
public class GhostNetRestController {

    private final IGhostNetBusinessLayerService service;
    private final GhostNetWebLayerMapper webMapper;
    private final GhostNetWebToBusinessMapper webToBusinessMapper;
    private final PersonWebToBusinessMapper personWebToBusinessMapper;

    public GhostNetRestController(IGhostNetBusinessLayerService service, GhostNetWebLayerMapper webMapper, GhostNetWebToBusinessMapper webToBusinessMapper, PersonWebToBusinessMapper personWebToBusinessMapper) {
        this.service = service;
        this.webMapper = webMapper;
        this.webToBusinessMapper = webToBusinessMapper;
        this.personWebToBusinessMapper = personWebToBusinessMapper;
    }

    /* ---- READ ---------------------------------------------------------- */

    /** All GhostNets (optionally filtered by status via query param). */
    @GetMapping
    public List<GhostNetWebLayerModel> findAll(@RequestParam(name = "status", required = false) String status) {
        if (status == null || status.isBlank()) {
            return webMapper.toWebModelList(service.findAll());
        }
        NetStatusBusinessLayerEnum enumStatus = NetStatusBusinessLayerEnum.valueOf(status.toUpperCase());
        return webMapper.toWebModelList(service.findByStatus(enumStatus));
    }

    /** GhostNets filtered by status (path style) - kept for backward compatibility. */
    @GetMapping("/status/{status}")
    public List<GhostNetWebLayerModel> findByStatus(@PathVariable String status) {
        NetStatusBusinessLayerEnum enumStatus = NetStatusBusinessLayerEnum.valueOf(status.toUpperCase());
        return webMapper.toWebModelList(service.findByStatus(enumStatus));
    }

    /** Single GhostNet by ID. */
    @GetMapping("/{id}")
    public GhostNetWebLayerModel findOne(@PathVariable Long id) {
        var b = service.findByIdOrThrow(id);
        return webMapper.toWebModel(b);
    }

    /* ---- CREATE ---------------------------------------------------------- */

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GhostNetWebLayerModel create(@Valid @RequestBody CreateGhostNetRequest req) {
        var b = webToBusinessMapper.toBusinessModel(req);
        var saved = service.save(b);
        return webMapper.toWebModel(saved);
    }

    /* ---- UPDATE (Transitions) ---------------------------------------------------------- */

    @PatchMapping("/{id}/reserve")
    public ResponseEntity<?> reserve(@PathVariable Long id, @Valid @RequestBody ReserveRequest req) {
        var b = service.findByIdOrThrow(id);
        var person = personWebToBusinessMapper.toBusinessModel(req.personName());
        b.assignTo(person);
        var saved = service.save(b);
        return ResponseEntity.ok(webMapper.toWebModel(saved));
    }

    @PatchMapping("/{id}/recover")
    public ResponseEntity<?> recover(@PathVariable Long id, @RequestBody RecoverRequest req) {
        var b = service.findByIdOrThrow(id);
        b.markAsRecovered();
        var saved = service.save(b);
        return ResponseEntity.ok(webMapper.toWebModel(saved));
    }
}