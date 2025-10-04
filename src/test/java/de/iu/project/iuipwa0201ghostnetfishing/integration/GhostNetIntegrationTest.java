package de.iu.project.iuipwa0201ghostnetfishing.integration;

import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Models.NetStatusBusinessLayerEnum;
import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Models.PersonBusinessLayerModel;
import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Services.GhostNetBusinessLayerService;
import de.iu.project.iuipwa0201ghostnetfishing.web.Mappers.GhostNetWebToBusinessMapper;
import de.iu.project.iuipwa0201ghostnetfishing.web.Models.CreateGhostNetRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test") // Assumes test profile with H2
@Transactional
class GhostNetIntegrationTest {

    @Autowired
    private GhostNetBusinessLayerService service;

    @Autowired
    private GhostNetWebToBusinessMapper webToBusinessMapper;

    @Test
    void createAndRetrieveGhostNet() {
        CreateGhostNetRequest request = new CreateGhostNetRequest("Test Location", 10.5, "John Doe");

        // Create via mapper and service
        var businessModel = webToBusinessMapper.toBusinessModel(request);
        var saved = service.save(businessModel);

        // Retrieve
        var retrievedOpt = service.findById(saved.getId());
        assertThat(retrievedOpt).isPresent();
        var retrieved = retrievedOpt.get();
        assertThat(retrieved.getLocation()).isEqualTo("Test Location");
        assertThat(retrieved.getSize()).isEqualTo(10.5);
        assertThat(retrieved.getStatus()).isEqualTo(NetStatusBusinessLayerEnum.REPORTED);
        assertThat(retrieved.getRecoveringPerson().getName()).isEqualTo("John Doe");
    }

    @Test
    void reserveAndRecoverGhostNet() {
        // Create
        CreateGhostNetRequest createReq = new CreateGhostNetRequest("Test Location", 10.5, null);
        var businessModel = webToBusinessMapper.toBusinessModel(createReq);
        var saved = service.save(businessModel);

        // Reserve
        var person = new PersonBusinessLayerModel();
        person.setName("John Doe");
        saved.assignTo(person);
        var reserved = service.save(saved);

        assertThat(reserved.getStatus()).isEqualTo(NetStatusBusinessLayerEnum.RECOVERY_PENDING);
        assertThat(reserved.getRecoveringPerson().getName()).isEqualTo("John Doe");

        // Recover
        reserved.markAsRecovered();
        var recovered = service.save(reserved);

        assertThat(recovered.getStatus()).isEqualTo(NetStatusBusinessLayerEnum.RECOVERED);
    }
}
