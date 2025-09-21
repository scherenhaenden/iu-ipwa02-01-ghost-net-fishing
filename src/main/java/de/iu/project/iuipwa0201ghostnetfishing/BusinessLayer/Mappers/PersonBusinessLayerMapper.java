package de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Mappers;

import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Models.PersonBusinessLayerModel;
import de.iu.project.iuipwa0201ghostnetfishing.DatabaseLayer.Models.PersonDataLayerModel;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * PersonBusinessLayerMapperV2
 * ---------------------------
 * Spring Bean (non-static) for mapping:
 *   DataLayer <-> BusinessLayer (Person).
 * Designed for constructor injection where needed.
 */
@Component
public class PersonBusinessLayerMapper {

    /* --------- Entity -> Business --------- */
    /**
     * Converts a PersonDataLayerModel entity to a PersonBusinessLayerModel.
     *
     * @param entity the PersonDataLayerModel to convert
     * @return the corresponding PersonBusinessLayerModel, or null if input is null
     */
    public PersonBusinessLayerModel toBusiness(PersonDataLayerModel entity) {
        if (entity == null) return null;
        PersonBusinessLayerModel model = new PersonBusinessLayerModel();
        model.setId(entity.getId());
        model.setName(entity.getName());
        model.setPhoneNumber(entity.getPhoneNumber());
        return model;
    }

    /**
     * Converts a list of PersonDataLayerModel entities to a list of PersonBusinessLayerModel.
     *
     * @param entities the list of PersonDataLayerModel to convert
     * @return the list of corresponding PersonBusinessLayerModel, or empty list if input is null
     */
    public List<PersonBusinessLayerModel> toBusinessList(List<PersonDataLayerModel> entities) {
        return (entities == null) ? List.of()
                : entities.stream().map(this::toBusiness).collect(Collectors.toList());
    }
    
    /**
     * Converts a list of PersonBusinessLayerModel to a list of PersonDataLayerModel.
     *
     * @param list the list of PersonBusinessLayerModel to convert
     * @return the list of corresponding PersonDataLayerModel, or empty list if input is null
     */
    public List<PersonDataLayerModel> toEntityList(List<PersonBusinessLayerModel> list) {
        return (list == null) ? List.of()
                : list.stream().map(this::toEntity).collect(Collectors.toList());
    }

    /* --------- Business -> Entity --------- */
    /**
     * Converts a PersonBusinessLayerModel to a PersonDataLayerModel entity.
     *
     * @param model the PersonBusinessLayerModel to convert
     * @return the corresponding PersonDataLayerModel, or null if input is null
     */
    public PersonDataLayerModel toEntity(PersonBusinessLayerModel model) {
        if (model == null) return null;
        PersonDataLayerModel entity = new PersonDataLayerModel();
        entity.setId(model.getId());
        entity.setName(model.getName());
        entity.setPhoneNumber(model.getPhoneNumber());
        return entity;
    }
}
