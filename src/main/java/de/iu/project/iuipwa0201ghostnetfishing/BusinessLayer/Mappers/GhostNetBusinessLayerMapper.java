package de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Mappers;

import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Models.GhostNetBusinessLayerModel;
import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Models.NetStatusBusinessLayerEnum;
import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Models.PersonBusinessLayerModel;
import de.iu.project.iuipwa0201ghostnetfishing.DatabaseLayer.Models.GhostNetDataLayerModel;
import de.iu.project.iuipwa0201ghostnetfishing.DatabaseLayer.Models.NetStatusDataLayerEnum;
import de.iu.project.iuipwa0201ghostnetfishing.DatabaseLayer.Models.PersonDataLayerModel;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * GhostNetBusinessLayerMapper
 * ---------------------------
 * Spring Bean for mapping:
 *   DataLayer <-> BusinessLayer (GhostNet).
 * Designed for constructor injection where needed.
 */
@Component
public class GhostNetBusinessLayerMapper {

    private final PersonBusinessLayerMapper personMapper;

    @Autowired
    public GhostNetBusinessLayerMapper(PersonBusinessLayerMapper personMapper) {
        this.personMapper = personMapper;
    }

    // ---------------------------------------------------------------------
    // Entity -> Business-Model
    // ---------------------------------------------------------------------

    /**
     * Converts a GhostNetDataLayerModel entity to a GhostNetBusinessLayerModel.
     *
     * @param entity the GhostNetDataLayerModel to convert
     * @return the corresponding GhostNetBusinessLayerModel, or null if input is null
     */
    public GhostNetBusinessLayerModel toBusinessModel(GhostNetDataLayerModel entity) {
        if (entity == null) {
            return null;
        }
        GhostNetBusinessLayerModel model = new GhostNetBusinessLayerModel();
        model.setId(entity.getId());
        model.setLocation(entity.getLocation());
        model.setSize(entity.getSize());
        model.setStatus(NetStatusBusinessLayerEnum.valueOf(entity.getStatus().name()));
        // Defensive: createdAt may be null in some edge cases; avoid NPE
        if (entity.getCreatedAt() != null) {
            model.setCreatedAt(entity.getCreatedAt().toInstant());
        } else {
            model.setCreatedAt(Instant.now());
        }
        model.setRecoveringPerson(
                personMapper.toBusiness(entity.getPerson())
        );
        return model;
    }

    /**
     * Converts a list of GhostNetDataLayerModel entities to a list of GhostNetBusinessLayerModel.
     *
     * @param entities the list of GhostNetDataLayerModel to convert
     * @return the list of corresponding GhostNetBusinessLayerModel, or empty list if input is null
     */
    public List<GhostNetBusinessLayerModel> toBusinessModelList(List<GhostNetDataLayerModel> entities) {
        return entities == null ? List.of()
                : entities.stream()
                .map(this::toBusinessModel)
                .collect(Collectors.toList());
    }

    // ---------------------------------------------------------------------
    // Business-Model -> Entity
    // ---------------------------------------------------------------------

    /**
     * Converts a GhostNetBusinessLayerModel to a GhostNetDataLayerModel entity.
     *
     * @param model the GhostNetBusinessLayerModel to convert
     * @return the corresponding GhostNetDataLayerModel, or null if input is null
     */
    public GhostNetDataLayerModel toEntity(GhostNetBusinessLayerModel model) {
        if (model == null) {
            return null;
        }
        // Map status safely (null-aware)
        NetStatusDataLayerEnum dataStatus = (model.getStatus() != null) ? NetStatusDataLayerEnum.valueOf(model.getStatus().name()) : null;
        // Map person
        PersonDataLayerModel personEntity = personMapper.toEntity(model.getRecoveringPerson());
        // Use constructor that preserves createdAt when null
        Date createdAtDate = (model.getCreatedAt() != null) ? Date.from(model.getCreatedAt()) : null;
        GhostNetDataLayerModel entity = new GhostNetDataLayerModel(
                model.getId(),
                model.getLocation(),
                model.getSize(),
                dataStatus,
                createdAtDate,
                personEntity
        );
        return entity;
    }
}