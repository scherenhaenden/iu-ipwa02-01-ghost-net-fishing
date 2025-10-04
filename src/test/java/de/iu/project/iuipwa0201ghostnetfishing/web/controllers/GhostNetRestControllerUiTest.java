package de.iu.project.iuipwa0201ghostnetfishing.web.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Models.GhostNetBusinessLayerModel;
import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Models.NetStatusBusinessLayerEnum;
import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Models.PersonBusinessLayerModel;
import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Services.GhostNetDomainService;
import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Services.IGhostNetBusinessLayerService;
import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Services.OperationResult;
import de.iu.project.iuipwa0201ghostnetfishing.web.Mappers.GhostNetWebLayerMapper;
import de.iu.project.iuipwa0201ghostnetfishing.web.Mappers.GhostNetWebToBusinessMapper;
import de.iu.project.iuipwa0201ghostnetfishing.web.Mappers.PersonWebToBusinessMapper;
import de.iu.project.iuipwa0201ghostnetfishing.web.Models.CreateGhostNetRequest;
import de.iu.project.iuipwa0201ghostnetfishing.web.Models.RecoverRequest;
import de.iu.project.iuipwa0201ghostnetfishing.web.Models.ReserveRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GhostNetRestController.class)
@Import({GhostNetWebLayerMapper.class, GhostNetWebToBusinessMapper.class, PersonWebToBusinessMapper.class})
class GhostNetRestControllerUiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IGhostNetBusinessLayerService service;

    @MockBean
    private GhostNetDomainService domainService;

    private GhostNetBusinessLayerModel sample;

    @BeforeEach
    void setUp() {
        sample = new GhostNetBusinessLayerModel();
        sample.setId(1L);
        sample.setLocation("Test Location");
        sample.setSize(10.5);
        sample.setStatus(NetStatusBusinessLayerEnum.REPORTED);
        sample.setCreatedAt(Instant.now());
    }

    @Test
    void testFindAllGhostNets() throws Exception {
        when(service.findAll()).thenReturn(List.of(sample));

        mockMvc.perform(get("/api/ghostnets")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].location").value("Test Location"));

        verify(service).findAll();
    }

    @Test
    void testFindGhostNetById() throws Exception {
        when(service.findByIdOrThrow(1L)).thenReturn(sample);

        mockMvc.perform(get("/api/ghostnets/1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.location").value("Test Location"));

        verify(service).findByIdOrThrow(1L);
    }

    @Test
    void testCreateGhostNetWithMapper() throws Exception {
        CreateGhostNetRequest request = new CreateGhostNetRequest("New Location", 5.0, null);

        GhostNetBusinessLayerModel created = new GhostNetBusinessLayerModel();
        created.setId(2L);
        created.setLocation("New Location");
        created.setSize(5.0);
        created.setStatus(NetStatusBusinessLayerEnum.REPORTED);
        created.setCreatedAt(Instant.now());

        when(service.save(any(GhostNetBusinessLayerModel.class))).thenReturn(created);

        mockMvc.perform(post("/api/ghostnets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.location").value("New Location"));

        verify(service).save(any(GhostNetBusinessLayerModel.class));
    }

    @Test
    void testReserveWithDomainService() throws Exception {
        ReserveRequest request = new ReserveRequest("John Doe");

        when(domainService.assignPerson(eq(1L), any(PersonBusinessLayerModel.class)))
                .thenReturn(OperationResult.OK);

        GhostNetBusinessLayerModel reserved = new GhostNetBusinessLayerModel();
        reserved.setId(1L);
        reserved.setStatus(NetStatusBusinessLayerEnum.RECOVERY_PENDING);
        PersonBusinessLayerModel person = new PersonBusinessLayerModel();
        person.setName("John Doe");
        reserved.setRecoveringPerson(person);

        when(domainService.findById(1L)).thenReturn(Optional.of(reserved));

        mockMvc.perform(patch("/api/ghostnets/1/reserve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("RECOVERY_PENDING"))
                .andExpect(jsonPath("$.recoveringPersonName").value("John Doe"));

        verify(domainService).assignPerson(eq(1L), any(PersonBusinessLayerModel.class));
    }

    @Test
    void testRecoverWithDomainService() throws Exception {
        RecoverRequest request = new RecoverRequest("Recovery completed");

        when(domainService.markRecovered(1L)).thenReturn(OperationResult.OK);

        GhostNetBusinessLayerModel recovered = new GhostNetBusinessLayerModel();
        recovered.setId(1L);
        recovered.setStatus(NetStatusBusinessLayerEnum.RECOVERED);

        when(domainService.findById(1L)).thenReturn(Optional.of(recovered));

        mockMvc.perform(patch("/api/ghostnets/1/recover")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("RECOVERED"));

        verify(domainService).markRecovered(1L);
    }

    @Test
    void testFindByStatusParameter() throws Exception {
        when(service.findByStatus(NetStatusBusinessLayerEnum.RECOVERY_PENDING))
                .thenReturn(List.of(sample));

        mockMvc.perform(get("/api/ghostnets")
                .param("status", "recovery_pending")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(service).findByStatus(NetStatusBusinessLayerEnum.RECOVERY_PENDING);
    }

    @Test
    void testFindByStatusPath() throws Exception {
        when(service.findByStatus(NetStatusBusinessLayerEnum.REPORTED))
                .thenReturn(List.of(sample));

        mockMvc.perform(get("/api/ghostnets/status/reported")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(service).findByStatus(NetStatusBusinessLayerEnum.REPORTED);
    }

    @Test
    void testReserveConflict() throws Exception {
        ReserveRequest request = new ReserveRequest("Jane Doe");

        when(domainService.assignPerson(eq(1L), any(PersonBusinessLayerModel.class)))
                .thenReturn(OperationResult.CONFLICT);

        mockMvc.perform(patch("/api/ghostnets/1/reserve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());

        verify(domainService).assignPerson(eq(1L), any(PersonBusinessLayerModel.class));
    }

    @Test
    void testRecoverConflict() throws Exception {
        RecoverRequest request = new RecoverRequest("Failed recovery");

        when(domainService.markRecovered(1L)).thenReturn(OperationResult.CONFLICT);

        mockMvc.perform(patch("/api/ghostnets/1/recover")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());

        verify(domainService).markRecovered(1L);
    }
}
