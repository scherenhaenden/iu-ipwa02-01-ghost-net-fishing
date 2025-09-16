package de.iu.project.iuipwa0201ghostnetfishing.web.Mappers;

import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Models.PersonBusinessLayerModel;
import de.iu.project.iuipwa0201ghostnetfishing.web.Models.PersonWebLayerModel;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Converts PersonBusinessLayerModel → PersonWebLayerModel.
 */
public final class PersonWebLayerMapper {

    private PersonWebLayerMapper() { throw new IllegalStateException("Utility class"); }

    /* Single object ------------------------------------------------------ */
    public static PersonWebLayerModel toWebModel(PersonBusinessLayerModel b) {
        if (b == null) return null;
        return new PersonWebLayerModel(b.getId(), b.getName(), b.getPhoneNumber());
    }

    /* List -------------------------------------------------------------- */
    public static List<PersonWebLayerModel> toWebModelList(List<PersonBusinessLayerModel> list) {
        return list == null ? List.of()
                : list.stream()
                .map(PersonWebLayerMapper::toWebModel)
                .collect(Collectors.toList());
    }
}