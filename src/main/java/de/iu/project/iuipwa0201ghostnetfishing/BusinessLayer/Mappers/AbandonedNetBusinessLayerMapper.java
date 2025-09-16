package de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Mappers;

import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Models.AbandonedNetBusinessLayerModel;
import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Models.NetStatusBusinessLayerEnum;
import de.iu.project.iuipwa0201ghostnetfishing.DatabaseLayer.Models.AbandonedNetDataLayerModel;
import de.iu.project.iuipwa0201ghostnetfishing.DatabaseLayer.Models.NetStatusDataLayerEnum;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * AbandonedNetBusinessLayerMapper
 * --------------------------------
 * Utility class to convert between
 * {@link AbandonedNetDataLayerModel} (JPA entity) and
 * {@link AbandonedNetBusinessLayerModel} (business POJO).
 */
public final class AbandonedNetBusinessLayerMapper {

    /** Utility class — instantiation not allowed. */
    private AbandonedNetBusinessLayerMapper() {
        throw new IllegalStateException("Utility class – do not instantiate");
    }

    // ---------------------------------------------------------------------
    // Entity -> Business-Model
    // ---------------------------------------------------------------------

    public static AbandonedNetBusinessLayerModel toBusinessModel(AbandonedNetDataLayerModel entity) {
        if (entity == null) {
            return null;
        }
        AbandonedNetBusinessLayerModel model = new AbandonedNetBusinessLayerModel();
        model.setId(entity.getId());
        model.setLocation(entity.getLocation());
        model.setSize(entity.getSize());
        model.setStatus(NetStatusBusinessLayerEnum.valueOf(entity.getStatus().name()));
        model.setCreatedAt(entity.getCreatedAt().toInstant());
        model.setPerson(
                PersonBusinessLayerMapper.toBusinessModel(entity.getPerson())
        );
        return model;
    }

    public static List<AbandonedNetBusinessLayerModel> toBusinessModelList(
            List<AbandonedNetDataLayerModel> entities) {
        return entities == null ? List.of()
                : entities.stream()
                .map(AbandonedNetBusinessLayerMapper::toBusinessModel)
                .collect(Collectors.toList());
    }

    // ---------------------------------------------------------------------
    // Business-Model -> Entity
    // ---------------------------------------------------------------------

    public static AbandonedNetDataLayerModel toEntity(AbandonedNetBusinessLayerModel model) {
        if (model == null) {
            return null;
        }

        AbandonedNetDataLayerModel entity = new AbandonedNetDataLayerModel(
                model.getId(),
                model.getLocation(),
                model.getSize(),
                NetStatusDataLayerEnum.valueOf(model.getStatus().name()),
                PersonBusinessLayerMapper.toEntity(model.getPerson())
        );

        // Preserve original timestamp if present
        if (model.getCreatedAt() != null) {
            entity.setCreatedAt(Date.from(model.getCreatedAt()));
        }

        return entity;
    }
}
