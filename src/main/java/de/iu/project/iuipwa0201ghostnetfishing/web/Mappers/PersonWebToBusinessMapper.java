package de.iu.project.iuipwa0201ghostnetfishing.web.Mappers;

import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Models.PersonBusinessLayerModel;
import org.springframework.stereotype.Component;

@Component
public class PersonWebToBusinessMapper {

    public PersonBusinessLayerModel toBusinessModel(String personName) {
        if (personName == null || personName.isBlank()) {
            return null;
        }
        PersonBusinessLayerModel person = new PersonBusinessLayerModel();
        person.setName(personName);
        return person;
    }
}
