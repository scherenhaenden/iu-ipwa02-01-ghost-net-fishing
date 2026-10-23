package de.iu.project.iuipwa0201ghostnetfishing.web.controllers;

import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Models.NetStatusBusinessLayerEnum;
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
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;

/**
 * REST API controller for managing GhostNet resources.
 * Provides endpoints for CRUD operations and status transitions on ghost nets.
 * This controller handles HTTP requests and responses for ghost net management,
 * interacting with the business layer services to perform operations.
 */
@RestController
@RequestMapping("/api/ghostnets")
@Validated
public class GhostNetRestController {

    private static final Logger log = LoggerFactory.getLogger(GhostNetRestController.class);

    private final IGhostNetBusinessLayerService service;
    private final GhostNetWebLayerMapper webMapper;
    private final GhostNetWebToBusinessMapper webToBusinessMapper;
    private final PersonWebToBusinessMapper personWebToBusinessMapper;

    // domainService is optional for backward compatibility in tests; if present we use it to map conflicts
    private final GhostNetDomainService domainService;

    /**
     * Constructs a new GhostNetRestController with the required dependencies.
     * The domainService is optional and may be null for backward compatibility.
     *
     * @param service the business layer service for ghost nets
     * @param webMapper mapper for converting business models to web models
     * @param webToBusinessMapper mapper for converting web requests to business models
     * @param personWebToBusinessMapper mapper for converting person web data to business models
     * @param domainServiceProvider provider for the optional domain service
     */
    // Use constructor injection for mandatory dependencies and ObjectProvider to keep domainService optional
    public GhostNetRestController(IGhostNetBusinessLayerService service,
                                 GhostNetWebLayerMapper webMapper,
                                 GhostNetWebToBusinessMapper webToBusinessMapper,
                                 PersonWebToBusinessMapper personWebToBusinessMapper,
                                 ObjectProvider<GhostNetDomainService> domainServiceProvider) {
        this.service = Objects.requireNonNull(service, "service must not be null");
        this.webMapper = Objects.requireNonNull(webMapper, "webMapper must not be null");
        this.webToBusinessMapper = Objects.requireNonNull(webToBusinessMapper, "webToBusinessMapper must not be null");
        this.personWebToBusinessMapper = Objects.requireNonNull(personWebToBusinessMapper, "personWebToBusinessMapper must not be null");
        // keep optional semantics: provider may not provide a bean in tests or older configurations
        this.domainService = domainServiceProvider != null ? domainServiceProvider.getIfAvailable() : null;
    }

    /* ---- READ ---------------------------------------------------------- */

    /**
     * Retrieves all ghost nets, optionally filtered by status via query parameter.
     *
     * @param status the status filter (optional, case-insensitive)
     * @return a list of GhostNetWebLayerModel objects
     */
    @GetMapping
    public List<GhostNetWebLayerModel> findAll(@RequestParam(name = "status", required = false) String status) {
        if (status == null || status.isBlank()) {
            return webMapper.toWebModelList(service.findAll());
        }
        NetStatusBusinessLayerEnum enumStatus = NetStatusBusinessLayerEnum.valueOf(status.toUpperCase());
        return webMapper.toWebModelList(service.findByStatus(enumStatus));
    }

    /**
     * Retrieves ghost nets filtered by status (path style) - kept for backward compatibility.
     *
     * @param status the status filter (case-insensitive)
     * @return a list of GhostNetWebLayerModel objects
     */
    @GetMapping("/status/{status}")
    public List<GhostNetWebLayerModel> findByStatus(@PathVariable String status) {
        NetStatusBusinessLayerEnum enumStatus = NetStatusBusinessLayerEnum.valueOf(status.toUpperCase());
        return webMapper.toWebModelList(service.findByStatus(enumStatus));
    }

    /**
     * Retrieves a single ghost net by its ID.
     *
     * @param id the ID of the ghost net
     * @return ResponseEntity containing the GhostNetWebLayerModel if found, or 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<GhostNetWebLayerModel> findOne(@PathVariable Long id) {
        return service.findById(id)
                .map(m -> ResponseEntity.ok(webMapper.toWebModel(m)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /* ---- CREATE ---------------------------------------------------------- */

    /**
     * Creates a new ghost net.
     *
     * @param req the create request containing ghost net details
     * @param ucb URI components builder for creating the location header
     * @return ResponseEntity with the created GhostNetWebLayerModel and location header
     */
    @PostMapping
    public ResponseEntity<GhostNetWebLayerModel> create(@Valid @RequestBody CreateGhostNetRequest req,
                                                         UriComponentsBuilder ucb) {
        var saved = service.save(webToBusinessMapper.toBusinessModel(req));
        var web = webMapper.toWebModel(saved);
        var location = ucb.path("/api/ghostnets/{id}").buildAndExpand(saved.getId()).toUri();
        return ResponseEntity.created(location).body(web);
    }

    /* ---- UPDATE (Transitions) ---------------------------------------------------------- */

    /**
     * Reserves a ghost net for recovery by assigning a person.
     *
     * @param id the ID of the ghost net
     * @param req the reserve request containing the person name
     * @return ResponseEntity with the updated GhostNetWebLayerModel or appropriate error status
     */
    @PatchMapping("/{id}/reserve")
    public ResponseEntity<?> reserve(@PathVariable Long id, @Valid @RequestBody ReserveRequest req) {
        // Prefer domainService if available to map OperationResult -> HTTP
        if (domainService != null) {
            var person = personWebToBusinessMapper.toBusinessModel(req.personName());
            var result = domainService.assignPerson(id, person);
            if (result != null) {
                return switch (result) {
                    case OperationResult.OK -> domainService.findById(id)
                            .map(m -> ResponseEntity.ok(webMapper.toWebModel(m)))
                            .orElseGet(() -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                    case OperationResult.NOT_FOUND -> ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                    case OperationResult.CONFLICT -> ResponseEntity.status(HttpStatus.CONFLICT).build();
                    case OperationResult.BAD_REQUEST -> ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
                };
            }
            // if result is null, fall through to fallback path below
        }
        // Fallback: try the new business service reserve method (OperationResult)
        var person = personWebToBusinessMapper.toBusinessModel(req.personName());
        var result = service.reserve(id, person);
        if (result == null) {
            // preserve previous behavior for backward compatibility (e.g. tests using a mock service)
            var ghostNetOpt = service.findById(id);
            if (ghostNetOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            var ghostNet = ghostNetOpt.get();
            // Minimal conflict guard: if already reserved or recovered, return 409
            if (ghostNet.getStatus() == NetStatusBusinessLayerEnum.RECOVERY_PENDING || ghostNet.getStatus() == NetStatusBusinessLayerEnum.RECOVERED) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            ghostNet.assignTo(person);
            var saved = service.save(ghostNet);
            return ResponseEntity.ok(webMapper.toWebModel(saved));
        }
        return switch (result) {
            case de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Services.OperationResult.OK -> service.findById(id)
                    .map(m -> ResponseEntity.ok(webMapper.toWebModel(m)))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
            case de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Services.OperationResult.NOT_FOUND -> ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            case de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Services.OperationResult.CONFLICT -> ResponseEntity.status(HttpStatus.CONFLICT).build();
            case de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Services.OperationResult.BAD_REQUEST -> ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        };
    }

    /**
     * Marks a ghost net as recovered.
     *
     * @param id the ID of the ghost net
     * @param req the recover request (optional, may contain notes)
     * @return ResponseEntity with the updated GhostNetWebLayerModel or appropriate error status
     */
    @PatchMapping("/{id}/recover")
    public ResponseEntity<?> recover(@PathVariable Long id, @RequestBody(required = false) RecoverRequest req) {
        // `req` is optional; we may log notes if provided (notes are optional and may not be persisted yet)
        String notes = (req == null) ? null : req.notes();
        if (notes != null && !notes.isBlank()) {
            log.info("Recover request for id {} with notes: {}", id, notes);
        }
        if (domainService != null) {
            var result = domainService.markRecovered(id);
            if (result != null) {
                return switch (result) {
                    case de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Services.OperationResult.OK -> domainService.findById(id)
                            .map(m -> ResponseEntity.ok(webMapper.toWebModel(m)))
                            .orElseGet(() -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                    case de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Services.OperationResult.NOT_FOUND -> ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                    case de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Services.OperationResult.CONFLICT -> ResponseEntity.status(HttpStatus.CONFLICT).build();
                    case de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Services.OperationResult.BAD_REQUEST -> ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
                };
            }
            // if result is null, fall through to fallback path below
        }
        // Use service.recover() method that returns OperationResult
        var result = service.recover(id);
        return switch (result) {
            case de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Services.OperationResult.OK -> service.findById(id)
                    .map(m -> ResponseEntity.ok(webMapper.toWebModel(m)))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
            case de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Services.OperationResult.NOT_FOUND -> ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            case de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Services.OperationResult.CONFLICT -> ResponseEntity.status(HttpStatus.CONFLICT).build();
            case de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Services.OperationResult.BAD_REQUEST -> ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        };
    }

    /**
     * Marks a ghost net as missing.
     *
     * @param id the ID of the ghost net
     * @return ResponseEntity with the updated GhostNetWebLayerModel or appropriate error status
     */
    @PatchMapping("/{id}/missing")
    public ResponseEntity<?> markAsMissing(@PathVariable Long id) {
        if (domainService != null) {
            var result = domainService.markAsMissing(id);
            if (result != null) {
                return switch (result) {
                    case de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Services.OperationResult.OK -> domainService.findById(id)
                            .map(m -> ResponseEntity.ok(webMapper.toWebModel(m)))
                            .orElseGet(() -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                    case de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Services.OperationResult.NOT_FOUND -> ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                    case de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Services.OperationResult.CONFLICT -> ResponseEntity.status(HttpStatus.CONFLICT).build();
                    case de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Services.OperationResult.BAD_REQUEST -> ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
                };
            }
            // if result is null, fall through to fallback path below
        }
        // Use service.markMissing() method that returns OperationResult
        var result = service.markMissing(id);
        return switch (result) {
            case de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Services.OperationResult.OK -> service.findById(id)
                    .map(m -> ResponseEntity.ok(webMapper.toWebModel(m)))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
            case de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Services.OperationResult.NOT_FOUND -> ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            case de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Services.OperationResult.CONFLICT -> ResponseEntity.status(HttpStatus.CONFLICT).build();
            case de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Services.OperationResult.BAD_REQUEST -> ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        };
    }
}