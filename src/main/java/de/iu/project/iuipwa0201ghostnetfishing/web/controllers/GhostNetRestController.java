package de.iu.project.iuipwa0201ghostnetfishing.web.controllers;

import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Models.NetStatusBusinessLayerEnum;
import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Services.GhostNetDomainService;
import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Services.IGhostNetBusinessLayerService;
import de.iu.project.iuipwa0201ghostnetfishing.web.Mappers.GhostNetWebLayerMapper;
import de.iu.project.iuipwa0201ghostnetfishing.web.Mappers.GhostNetWebToBusinessMapper;
import de.iu.project.iuipwa0201ghostnetfishing.web.Mappers.PersonWebToBusinessMapper;
import de.iu.project.iuipwa0201ghostnetfishing.web.Models.CreateGhostNetRequest;
import de.iu.project.iuipwa0201ghostnetfishing.web.Models.GhostNetWebLayerModel;
import de.iu.project.iuipwa0201ghostnetfishing.web.Models.RecoverRequest;
import de.iu.project.iuipwa0201ghostnetfishing.web.Models.ReserveRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

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

    // domainService is optional for backward compatibility in tests; if present we use it to map conflicts
    @Autowired(required = false)
    private GhostNetDomainService domainService;

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
        var ghostNet = service.findByIdOrThrow(id);
        return webMapper.toWebModel(ghostNet);
    }

    /* ---- CREATE ---------------------------------------------------------- */

    @PostMapping
    public ResponseEntity<GhostNetWebLayerModel> create(@Valid @RequestBody CreateGhostNetRequest req,
                                                         UriComponentsBuilder ucb) {
        var saved = service.save(webToBusinessMapper.toBusinessModel(req));
        var web = webMapper.toWebModel(saved);
        var location = ucb.path("/api/ghostnets/{id}").buildAndExpand(saved.getId()).toUri();
        return ResponseEntity.created(location).body(web);
    }

    /* ---- UPDATE (Transitions) ---------------------------------------------------------- */

    @PatchMapping("/{id}/reserve")
    public ResponseEntity<?> reserve(@PathVariable Long id, @Valid @RequestBody ReserveRequest req) {
        // Prefer domainService if available to map OperationResult -> HTTP
        if (domainService != null) {
            var person = personWebToBusinessMapper.toBusinessModel(req.personName());
            var result = domainService.assignPerson(id, person);
            return switch (result) {
                case OK -> domainService.findById(id)
                        .map(m -> ResponseEntity.ok(webMapper.toWebModel(m)))
                        .orElseGet(() -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                case NOT_FOUND -> ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                case CONFLICT -> ResponseEntity.status(HttpStatus.CONFLICT).build();
                case BAD_REQUEST -> ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            };
        }
        // Fallback: try the new business service reserve method (OperationResult)
        var person = personWebToBusinessMapper.toBusinessModel(req.personName());
        var result = service.reserve(id, person);
        if (result == null) {
            // preserve previous behavior for backward compatibility (e.g. tests using a mock service)
            var ghostNet = service.findByIdOrThrow(id);
            // Minimal conflict guard: if already reserved or recovered, return 409
            if (ghostNet.getStatus() == NetStatusBusinessLayerEnum.RECOVERY_PENDING || ghostNet.getStatus() == NetStatusBusinessLayerEnum.RECOVERED) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            ghostNet.assignTo(person);
            var saved = service.save(ghostNet);
            return ResponseEntity.ok(webMapper.toWebModel(saved));
        }
        return switch (result) {
            case OK -> service.findById(id)
                    .map(m -> ResponseEntity.ok(webMapper.toWebModel(m)))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
            case NOT_FOUND -> ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            case CONFLICT -> ResponseEntity.status(HttpStatus.CONFLICT).build();
            case BAD_REQUEST -> ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        };
    }

    @PatchMapping("/{id}/recover")
    public ResponseEntity<?> recover(@PathVariable Long id, @RequestBody RecoverRequest req) {
        if (domainService != null) {
            var result = domainService.markRecovered(id);
            return switch (result) {
                case OK -> domainService.findById(id)
                        .map(m -> ResponseEntity.ok(webMapper.toWebModel(m)))
                        .orElseGet(() -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                case NOT_FOUND -> ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                case CONFLICT -> ResponseEntity.status(HttpStatus.CONFLICT).build();
                case BAD_REQUEST -> ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            };
        }
        var ghostNet = service.findByIdOrThrow(id);
        ghostNet.markAsRecovered();
        var saved = service.save(ghostNet);
        return ResponseEntity.ok(webMapper.toWebModel(saved));
    }
}