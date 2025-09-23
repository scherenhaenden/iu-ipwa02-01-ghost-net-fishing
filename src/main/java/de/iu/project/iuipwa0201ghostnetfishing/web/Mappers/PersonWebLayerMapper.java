package de.iu.project.iuipwa0201ghostnetfishing.web.Mappers;

import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Models.PersonBusinessLayerModel;
import de.iu.project.iuipwa0201ghostnetfishing.web.Models.PersonWebLayerModel;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

/**
 * Converts PersonBusinessLayerModel → PersonWebLayerModel.
 */
@Component
public class PersonWebLayerMapper {

    /* Single object ------------------------------------------------------ */
    public PersonWebLayerModel toWebModel(PersonBusinessLayerModel b) {
        if (b == null) return null;
        return new PersonWebLayerModel(b.getId(), b.getName(), b.getPhoneNumber());
    }

    /* List -------------------------------------------------------------- */
    public List<PersonWebLayerModel> toWebModelList(List<PersonBusinessLayerModel> list) {
        return list == null ? List.of()
                : list.stream()
                .map(this::toWebModel)
                .collect(Collectors.toList());
    }
}