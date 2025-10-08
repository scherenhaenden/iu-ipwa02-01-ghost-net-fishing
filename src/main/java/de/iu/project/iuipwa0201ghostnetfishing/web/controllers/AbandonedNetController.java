package de.iu.project.iuipwa0201ghostnetfishing.web.controllers;

import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Models.AbandonedNetBusinessLayerModel;
import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Models.NetStatusBusinessLayerEnum;
import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Models.PersonBusinessLayerModel;
import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Services.IAbandonedNetBusinessLayerService;
import de.iu.project.iuipwa0201ghostnetfishing.web.Mappers.AbandonedNetWebLayerMapper;
import de.iu.project.iuipwa0201ghostnetfishing.web.Models.AbandonedNetWebLayerModel;
import de.iu.project.iuipwa0201ghostnetfishing.web.Models.CreateAbandonedNetRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/abandoned-nets")
public class AbandonedNetController {

    private final IAbandonedNetBusinessLayerService service;
    private final AbandonedNetWebLayerMapper mapper;

    public AbandonedNetController(IAbandonedNetBusinessLayerService service, AbandonedNetWebLayerMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<AbandonedNetWebLayerModel> create(@RequestBody @Valid CreateAbandonedNetRequest req, UriComponentsBuilder ucb) {
        AbandonedNetBusinessLayerModel b = new AbandonedNetBusinessLayerModel();
        b.setLocation(req.getLocation());
        b.setSize(req.getSize());
        b.setStatus(NetStatusBusinessLayerEnum.REPORTED);
        b.setCreatedAt(java.time.Instant.now());

        if (req.getPersonName() != null && !req.getPersonName().isBlank()) {
            PersonBusinessLayerModel p = new PersonBusinessLayerModel();
            p.setName(req.getPersonName());
            b.setPerson(p);
        }

        AbandonedNetBusinessLayerModel saved = service.save(b);
        AbandonedNetWebLayerModel web = mapper.toWebModel(saved);
        // Location updated to the abandoned-nets base to avoid collision with GhostNet REST controller
        URI location = ucb.path("/api/abandoned-nets/{id}").buildAndExpand(saved.getId()).toUri();
        return ResponseEntity.created(location).body(web);
    }

    @GetMapping
    public ResponseEntity<List<AbandonedNetWebLayerModel>> list(@RequestParam(name = "status", required = false) String status) {
        List<AbandonedNetBusinessLayerModel> list;
        if (status == null || status.isBlank()) {
            list = service.getAllNetsNewestFirst();
        } else {
            NetStatusBusinessLayerEnum enumStatus = NetStatusBusinessLayerEnum.valueOf(status.trim().toUpperCase());
            list = service.findByStatus(enumStatus);
        }
        List<AbandonedNetWebLayerModel> webList = list.stream().map(mapper::toWebModel).collect(Collectors.toList());
        return ResponseEntity.ok(webList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AbandonedNetWebLayerModel> getById(@PathVariable("id") Long id) {
        AbandonedNetBusinessLayerModel found = service.findById(id);
        return ResponseEntity.ok(mapper.toWebModel(found));
    }
}
