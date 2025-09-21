package de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Mappers;

import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Models.AbandonedNetBusinessLayerModel;
import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Models.NetStatusBusinessLayerEnum;
import de.iu.project.iuipwa0201ghostnetfishing.DatabaseLayer.Models.AbandonedNetDataLayerModel;
import de.iu.project.iuipwa0201ghostnetfishing.DatabaseLayer.Models.NetStatusDataLayerEnum;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * AbandonedNetBusinessLayerMapper
 * --------------------------------
 * Spring Bean for mapping:
 *   DataLayer &lt;-&gt; BusinessLayer (AbandonedNet).
 * Designed for constructor injection where needed.
 */
@Component
public class AbandonedNetBusinessLayerMapper {

    private final PersonBusinessLayerMapper personMapper;

    @Autowired
    public AbandonedNetBusinessLayerMapper(PersonBusinessLayerMapper personMapper) {
        this.personMapper = personMapper;
    }

    // ---------------------------------------------------------------------
    // Entity -> Business-Model
    // ---------------------------------------------------------------------

    /**
     * Converts an AbandonedNetDataLayerModel entity to an AbandonedNetBusinessLayerModel.
     *
     * @param entity the AbandonedNetDataLayerModel to convert
     * @return the corresponding AbandonedNetBusinessLayerModel, or null if input is null
     */
    public AbandonedNetBusinessLayerModel toBusinessModel(AbandonedNetDataLayerModel entity) {
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
                personMapper.toBusiness(entity.getPerson())
        );
        return model;
    }

    /**
     * Converts a list of AbandonedNetDataLayerModel entities to a list of AbandonedNetBusinessLayerModel.
     *
     * @param entities the list of AbandonedNetDataLayerModel to convert
     * @return the list of corresponding AbandonedNetBusinessLayerModel, or empty list if input is null
     */
    public List<AbandonedNetBusinessLayerModel> toBusinessModelList(
            List<AbandonedNetDataLayerModel> entities) {
        return entities == null ? List.of()
                : entities.stream()
                .map(this::toBusinessModel)
                .collect(Collectors.toList());
    }

    // ---------------------------------------------------------------------
    // Business-Model -> Entity
    // ---------------------------------------------------------------------

    /**
     * Converts an AbandonedNetBusinessLayerModel to an AbandonedNetDataLayerModel entity.
     *
     * @param model the AbandonedNetBusinessLayerModel to convert
     * @return the corresponding AbandonedNetDataLayerModel, or null if input is null
     */
    public AbandonedNetDataLayerModel toEntity(AbandonedNetBusinessLayerModel model) {
        if (model == null) {
            return null;
        }

        // Map status safely (null-aware)
        NetStatusDataLayerEnum dataStatus = (model.getStatus() != null) ? NetStatusDataLayerEnum.valueOf(model.getStatus().name()) : null;
        // Map person
        var personEntity = personMapper.toEntity(model.getPerson());
        // Preserve createdAt if present, otherwise leave null
        java.util.Date createdAt = (model.getCreatedAt() != null) ? java.util.Date.from(model.getCreatedAt()) : null;

        AbandonedNetDataLayerModel entity = new AbandonedNetDataLayerModel(
                model.getId(),
                model.getLocation(),
                model.getSize(),
                dataStatus,
                createdAt,
                personEntity
        );

        return entity;
    }
}
