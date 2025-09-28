package de.iu.project.iuipwa0201ghostnetfishing.web.controllers;

import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Models.GhostNetBusinessLayerModel;
import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Models.NetStatusBusinessLayerEnum;
import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Models.PersonBusinessLayerModel;
import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Services.GhostNetDomainService;
import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Services.IGhostNetBusinessLayerService;
import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Services.OperationResult;
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
import java.util.stream.Collectors;

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
    private final GhostNetDomainService domainService;

    public GhostNetRestController(IGhostNetBusinessLayerService service, GhostNetWebLayerMapper webMapper, GhostNetWebToBusinessMapper webToBusinessMapper, PersonWebToBusinessMapper personWebToBusinessMapper, GhostNetDomainService domainService) {
        this.service = service;
        this.webMapper = webMapper;
        this.webToBusinessMapper = webToBusinessMapper;
        this.personWebToBusinessMapper = personWebToBusinessMapper;
        this.domainService = domainService;
    }

    /* ---- READ ---------------------------------------------------------- */

    /** All GhostNets (unsorted). */
    @GetMapping
    public List<GhostNetWebLayerModel> findAll() {
        return webMapper.toWebModelList(service.findAll());
    }

    /** GhostNets filtered by status (e.g. REPORTED). */
    @GetMapping("/status/{status}")
    public List<GhostNetWebLayerModel> findByStatus(@PathVariable String status) {
        NetStatusBusinessLayerEnum enumStatus =
                NetStatusBusinessLayerEnum.valueOf(status.toUpperCase());
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
        PersonBusinessLayerModel person = personWebToBusinessMapper.toBusinessModel(req.personName());
        OperationResult result = domainService.assignPerson(id, person);
        if (result == OperationResult.OK) {
            Optional<GhostNetBusinessLayerModel> updated = domainService.findById(id);
            return updated.map(m -> ResponseEntity.ok(webMapper.toWebModel(m)))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
        } else if (result == OperationResult.NOT_FOUND) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PatchMapping("/{id}/recover")
    public ResponseEntity<?> recover(@PathVariable Long id, @RequestBody RecoverRequest req) {
        OperationResult result = domainService.markRecovered(id);
        if (result == OperationResult.OK) {
            Optional<GhostNetBusinessLayerModel> updated = domainService.findById(id);
            return updated.map(m -> ResponseEntity.ok(webMapper.toWebModel(m)))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
        } else if (result == OperationResult.NOT_FOUND) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }
}