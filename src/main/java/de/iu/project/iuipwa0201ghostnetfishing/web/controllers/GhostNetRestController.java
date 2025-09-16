package de.iu.project.iuipwa0201ghostnetfishing.web.controllers;

import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Models.GhostNetBusinessLayerModel;
import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Models.NetStatusBusinessLayerEnum;
import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Services.IGhostNetBusinessLayerService;
import de.iu.project.iuipwa0201ghostnetfishing.web.Models.GhostNetWebLayerModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST API for GhostNet resources.
 */
@RestController
@RequestMapping("/api/ghostnets")
public class GhostNetRestController {

    private final IGhostNetBusinessLayerService service;

    public GhostNetRestController(IGhostNetBusinessLayerService service) {
        this.service = service;
    }

    /* ---- READ ---------------------------------------------------------- */

    /** All GhostNets (unsorted). */
    @GetMapping
    public List<GhostNetWebLayerModel> findAll() {
        return service.findAll()
                .stream()
                .map(this::toWebModel)
                .collect(Collectors.toList());
    }

    /** GhostNets filtered by status (e.g. REPORTED). */
    @GetMapping("/status/{status}")
    public List<GhostNetWebLayerModel> findByStatus(@PathVariable String status) {
        NetStatusBusinessLayerEnum enumStatus =
                NetStatusBusinessLayerEnum.valueOf(status.toUpperCase());
        return service.findByStatus(enumStatus)
                .stream()
                .map(this::toWebModel)
                .collect(Collectors.toList());
    }

    /* ---- Mapping Helper ----------------------------------------------- */

    private GhostNetWebLayerModel toWebModel(GhostNetBusinessLayerModel b) {
        return new GhostNetWebLayerModel(
                b.getId(),
                b.getLocation(),
                b.getSize(),
                b.getStatus().name(),
                b.getCreatedAt(),
                b.getRecoveringPerson() != null ? b.getRecoveringPerson().getName() : null
        );
    }
}