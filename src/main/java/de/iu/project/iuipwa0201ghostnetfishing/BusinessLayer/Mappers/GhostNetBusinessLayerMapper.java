package de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Mappers;

import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Models.GhostNetBusinessLayerModel;
import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Models.NetStatusBusinessLayerEnum;
import de.iu.project.iuipwa0201ghostnetfishing.DatabaseLayer.Models.GhostNetDataLayerModel;
import de.iu.project.iuipwa0201ghostnetfishing.DatabaseLayer.Models.NetStatusDataLayerEnum;

import java.util.List;
import java.util.stream.Collectors;

/**
 * GhostNetBusinessLayerMapper
 * ---------------------------
 * Converts between GhostNetDataLayerModel (JPA entity)
 * and GhostNetBusinessLayerModel (business POJO).
 */
public final class GhostNetBusinessLayerMapper {

    /** Utility class — instantiation not allowed. */
    private GhostNetBusinessLayerMapper() {
        throw new IllegalStateException("Utility class — do not instantiate");
    }

    // ---------------------------------------------------------------------
    // Entity  -> Business-Model
    // ---------------------------------------------------------------------

    public static GhostNetBusinessLayerModel toBusinessModel(GhostNetDataLayerModel entity) {
        if (entity == null) {
            return null;
        }
        GhostNetBusinessLayerModel model = new GhostNetBusinessLayerModel();
        model.setId(entity.getId());
        model.setLocation(entity.getLocation());
        model.setSize(entity.getSize());
        model.setStatus(NetStatusBusinessLayerEnum.valueOf(entity.getStatus().name()));
        model.setCreatedAt(entity.getCreatedAt().toInstant());
        model.setRecoveringPerson(
                PersonBusinessLayerMapper.toBusinessModel(entity.getPerson())
        );
        return model;
    }

    public static List<GhostNetBusinessLayerModel> toBusinessModelList(List<GhostNetDataLayerModel> entities) {
        return entities == null ? List.of()
                : entities.stream()
                .map(GhostNetBusinessLayerMapper::toBusinessModel)
                .collect(Collectors.toList());
    }

    // ---------------------------------------------------------------------
    // Business-Model -> Entity
    // ---------------------------------------------------------------------

    public static GhostNetDataLayerModel toEntity(GhostNetBusinessLayerModel model) {
        if (model == null) {
            return null;
        }
        return new GhostNetDataLayerModel(
                model.getId(),
                model.getLocation(),
                model.getSize(),
                NetStatusDataLayerEnum.valueOf(model.getStatus().name()),
                PersonBusinessLayerMapper.toEntity(model.getRecoveringPerson())
        );
    }
}