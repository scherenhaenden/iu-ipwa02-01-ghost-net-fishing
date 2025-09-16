package de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Mappers;

import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Models.PersonBusinessLayerModel;
import de.iu.project.iuipwa0201ghostnetfishing.DatabaseLayer.Models.PersonDataLayerModel;

import java.util.List;
import java.util.stream.Collectors;

/**
 * PersonBusinessLayerMapper
 * -------------------------
 * Reine Utility-Klasse zum Konvertieren zwischen
 * PersonDataLayerModel (JPA-Entity) und PersonBusinessLayerModel (Business-POJO).
 */
public final class PersonBusinessLayerMapper {

    /** Utility-Klasse darf nicht instanziiert werden. */
    private PersonBusinessLayerMapper() {
        throw new IllegalStateException("Utility class – do not instantiate");
    }

    // ---------------------------------------------------------------------
    // Entity  -> Business-Model
    // ---------------------------------------------------------------------

    public static PersonBusinessLayerModel toBusinessModel(PersonDataLayerModel entity) {
        if (entity == null) {
            return null;
        }
        PersonBusinessLayerModel model = new PersonBusinessLayerModel();
        model.setId(entity.getId());
        model.setName(entity.getName());
        model.setPhoneNumber(entity.getPhoneNumber());
        return model;
    }

    public static List<PersonBusinessLayerModel> toBusinessModelList(List<PersonDataLayerModel> entities) {
        return entities == null ? List.of()
                : entities.stream()
                .map(PersonBusinessLayerMapper::toBusinessModel)
                .collect(Collectors.toList());
    }

    // ---------------------------------------------------------------------
    // Business-Model -> Entity
    // ---------------------------------------------------------------------

    public static PersonDataLayerModel toEntity(PersonBusinessLayerModel model) {
        if (model == null) {
            return null;
        }
        PersonDataLayerModel entity = new PersonDataLayerModel();
        entity.setId(model.getId());
        entity.setName(model.getName());
        entity.setPhoneNumber(model.getPhoneNumber());
        return entity;
    }
}