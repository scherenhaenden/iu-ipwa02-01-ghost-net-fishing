package de.iu.project.iuipwa0201ghostnetfishing.web.Mappers;

import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Models.GhostNetBusinessLayerModel;
import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Models.NetStatusBusinessLayerEnum;
import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Models.PersonBusinessLayerModel;
import de.iu.project.iuipwa0201ghostnetfishing.web.Models.CreateGhostNetRequest;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class GhostNetWebToBusinessMapper {

    public GhostNetBusinessLayerModel toBusinessModel(CreateGhostNetRequest request) {
        GhostNetBusinessLayerModel model = new GhostNetBusinessLayerModel();
        model.setLocation(request.location());
        model.setSize(request.size());
        model.setStatus(NetStatusBusinessLayerEnum.REPORTED);
        model.setCreatedAt(Instant.now());
        if (request.personName() != null && !request.personName().isBlank()) {
            PersonBusinessLayerModel person = new PersonBusinessLayerModel();
            person.setName(request.personName());
            model.setRecoveringPerson(person);
        }
        return model;
    }
}
