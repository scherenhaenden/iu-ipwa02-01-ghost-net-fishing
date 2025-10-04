package de.iu.project.iuipwa0201ghostnetfishing.web.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Models.GhostNetBusinessLayerModel;
import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Models.NetStatusBusinessLayerEnum;
import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Models.PersonBusinessLayerModel;
import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Services.GhostNetDomainService;
import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Services.IGhostNetBusinessLayerService;
import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Services.OperationResult;
import de.iu.project.iuipwa0201ghostnetfishing.web.Models.CreateGhostNetRequest;
import de.iu.project.iuipwa0201ghostnetfishing.web.Models.RecoverRequest;
import de.iu.project.iuipwa0201ghostnetfishing.web.Models.ReserveRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
@org.springframework.context.annotation.Import({
        de.iu.project.iuipwa0201ghostnetfishing.web.Mappers.GhostNetWebLayerMapper.class,
        de.iu.project.iuipwa0201ghostnetfishing.web.Mappers.GhostNetWebToBusinessMapper.class,
        de.iu.project.iuipwa0201ghostnetfishing.web.Mappers.PersonWebToBusinessMapper.class
})
class GhostNetRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IGhostNetBusinessLayerService service;

    @MockBean
    private GhostNetDomainService domainService;

    @Autowired
    private ObjectMapper objectMapper;

    private GhostNetBusinessLayerModel sampleNet;

    @BeforeEach
    void setUp() {
        sampleNet = new GhostNetBusinessLayerModel();
        sampleNet.setId(1L);
        sampleNet.setLocation("Sample Location");
        sampleNet.setSize(10.5);
        sampleNet.setStatus(NetStatusBusinessLayerEnum.REPORTED);
        sampleNet.setCreatedAt(Instant.now());
    }

    @Test
    void createGhostNet_Success() throws Exception {
        CreateGhostNetRequest request = new CreateGhostNetRequest("Test Location", 5.0, null);
        GhostNetBusinessLayerModel saved = new GhostNetBusinessLayerModel();
        saved.setId(1L);
        saved.setLocation("Test Location");
        saved.setSize(5.0);
        saved.setStatus(NetStatusBusinessLayerEnum.REPORTED);
        saved.setCreatedAt(Instant.now());
        when(service.save(any(GhostNetBusinessLayerModel.class))).thenReturn(saved);

        mockMvc.perform(post("/api/ghostnets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.location").value("Test Location"))
                .andExpect(jsonPath("$.size").value(5.0));

        verify(service, times(1)).save(any(GhostNetBusinessLayerModel.class));
    }

    @Test
    void createGhostNet_WithPersonName() throws Exception {
        CreateGhostNetRequest request = new CreateGhostNetRequest("Test Location", 5.0, "John Doe");
        GhostNetBusinessLayerModel saved = new GhostNetBusinessLayerModel();
        saved.setId(1L);
        saved.setLocation("Test Location");
        saved.setSize(5.0);
        saved.setStatus(NetStatusBusinessLayerEnum.REPORTED);
        saved.setCreatedAt(Instant.now());
        PersonBusinessLayerModel person = new PersonBusinessLayerModel();
        person.setName("John Doe");
        saved.setRecoveringPerson(person);
        when(service.save(any(GhostNetBusinessLayerModel.class))).thenReturn(saved);

        mockMvc.perform(post("/api/ghostnets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.recoveringPersonName").value("John Doe"));

        verify(service, times(1)).save(any(GhostNetBusinessLayerModel.class));
    }

    @Test
    void createGhostNet_InvalidLocation() throws Exception {
        CreateGhostNetRequest request = new CreateGhostNetRequest("", 5.0, null);

        mockMvc.perform(post("/api/ghostnets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));
    }

    @Test
    void createGhostNet_returnsLocationHeader() throws Exception {
        CreateGhostNetRequest request = new CreateGhostNetRequest("L1", 1.0, null);
        GhostNetBusinessLayerModel saved = new GhostNetBusinessLayerModel();
        saved.setId(42L);
        saved.setLocation("L1");
        saved.setSize(1.0);
        saved.setStatus(NetStatusBusinessLayerEnum.REPORTED);
        saved.setCreatedAt(Instant.now());
        when(service.save(any(GhostNetBusinessLayerModel.class))).thenReturn(saved);

        mockMvc.perform(post("/api/ghostnets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", org.hamcrest.Matchers.endsWith("/api/ghostnets/42")));
    }

    @Test
    void findAll_noStatus_usesFindAll() throws Exception {
        when(service.findAll()).thenReturn(List.of(sampleNet));

        mockMvc.perform(get("/api/ghostnets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].location").value("Sample Location"));

        verify(service, times(1)).findAll();
    }

    @Test
    void findAll_withStatusParam_usesFindByStatus() throws Exception {
        when(service.findByStatus(NetStatusBusinessLayerEnum.REPORTED)).thenReturn(List.of(sampleNet));

        mockMvc.perform(get("/api/ghostnets?status=reported"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("REPORTED"));

        verify(service, times(1)).findByStatus(NetStatusBusinessLayerEnum.REPORTED);
    }

    @Test
    void findByStatus_path_isHandled() throws Exception {
        when(service.findByStatus(NetStatusBusinessLayerEnum.REPORTED)).thenReturn(List.of(sampleNet));

        mockMvc.perform(get("/api/ghostnets/status/reported"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("REPORTED"));

        verify(service, times(1)).findByStatus(NetStatusBusinessLayerEnum.REPORTED);
    }

    @Test
    void reserveGhostNet_Success() throws Exception {
        ReserveRequest request = new ReserveRequest("John Doe");
        when(service.findByIdOrThrow(eq(1L))).thenReturn(sampleNet);
        GhostNetBusinessLayerModel updated = new GhostNetBusinessLayerModel();
        updated.setId(1L);
        updated.setStatus(NetStatusBusinessLayerEnum.RECOVERY_PENDING);
        PersonBusinessLayerModel person = new PersonBusinessLayerModel();
        person.setName("John Doe");
        updated.setRecoveringPerson(person);
        when(service.save(any(GhostNetBusinessLayerModel.class))).thenReturn(updated);

        mockMvc.perform(patch("/api/ghostnets/1/reserve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("RECOVERY_PENDING"))
                .andExpect(jsonPath("$.recoveringPersonName").value("John Doe"));

        verify(service, times(1)).findByIdOrThrow(eq(1L));
        verify(service, times(1)).save(any(GhostNetBusinessLayerModel.class));
    }

    @Test
    void reserveGhostNet_InvalidPersonName() throws Exception {
        ReserveRequest request = new ReserveRequest("");

        mockMvc.perform(patch("/api/ghostnets/1/reserve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));
    }

    @Test
    void reserveGhostNet_Conflict() throws Exception {
        ReserveRequest request = new ReserveRequest("John Doe");
        // simulate existing net already reserved
        sampleNet.setStatus(NetStatusBusinessLayerEnum.RECOVERY_PENDING);
        when(domainService.assignPerson(eq(1L), any())).thenReturn(OperationResult.CONFLICT);
        when(service.findByIdOrThrow(eq(1L))).thenReturn(sampleNet);

        mockMvc.perform(patch("/api/ghostnets/1/reserve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());

        // verify(service, times(1)).findByIdOrThrow(eq(1L))); // This is part of the fallback logic, not the domainService path
        // verify(service, times(0)).save(any(GhostNetBusinessLayerModel.class));
    }

    @Test
    void reserveGhostNet_serviceReturnsNotFound_mapsTo404() throws Exception {
        ReserveRequest request = new ReserveRequest("John Doe");
        when(service.reserve(eq(1L), any())).thenReturn(OperationResult.NOT_FOUND);

        mockMvc.perform(patch("/api/ghostnets/1/reserve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        verify(service, times(1)).reserve(eq(1L), any());
    }

    @Test
    void reserveGhostNet_serviceReturnsConflict_mapsTo409() throws Exception {
        ReserveRequest request = new ReserveRequest("John Doe");
        when(service.reserve(eq(1L), any())).thenReturn(OperationResult.CONFLICT);

        mockMvc.perform(patch("/api/ghostnets/1/reserve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());

        verify(service, times(1)).reserve(eq(1L), any());
    }

    @Test
    void recoverGhostNet_Success() throws Exception {
        RecoverRequest request = new RecoverRequest("Recovery notes");
        sampleNet.setStatus(NetStatusBusinessLayerEnum.RECOVERY_PENDING);
        when(service.findByIdOrThrow(eq(1L))).thenReturn(sampleNet);
        GhostNetBusinessLayerModel updated = new GhostNetBusinessLayerModel();
        updated.setId(1L);
        updated.setStatus(NetStatusBusinessLayerEnum.RECOVERED);
        when(service.save(any(GhostNetBusinessLayerModel.class))).thenReturn(updated);

        mockMvc.perform(patch("/api/ghostnets/1/recover")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("RECOVERED"));

        verify(service, times(1)).findByIdOrThrow(eq(1L));
        verify(service, times(1)).save(any(GhostNetBusinessLayerModel.class));
    }

    @Test
    void findOne_Success() throws Exception {
        when(service.findByIdOrThrow(eq(1L))).thenReturn(sampleNet);

        mockMvc.perform(get("/api/ghostnets/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.location").value("Sample Location"));
    }

    @Test
    void findOne_NotFound() throws Exception {
        when(service.findByIdOrThrow(eq(999L))).thenThrow(new de.iu.project.iuipwa0201ghostnetfishing.exceptions.ResourceNotFoundException("Not found"));

        mockMvc.perform(get("/api/ghostnets/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("NOT_FOUND"));
    }

    @Test
    void reserveGhostNet_domainService_OK_returns200() throws Exception {
        ReserveRequest request = new ReserveRequest("Jane");
        when(domainService.assignPerson(eq(1L), any())).thenReturn(OperationResult.OK);
        GhostNetBusinessLayerModel updated = new GhostNetBusinessLayerModel();
        updated.setId(1L);
        updated.setStatus(NetStatusBusinessLayerEnum.RECOVERY_PENDING);
        PersonBusinessLayerModel person = new PersonBusinessLayerModel();
        person.setName("Jane");
        updated.setRecoveringPerson(person);
        when(domainService.findById(eq(1L))).thenReturn(java.util.Optional.of(updated));

        mockMvc.perform(patch("/api/ghostnets/1/reserve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("RECOVERY_PENDING"))
                .andExpect(jsonPath("$.recoveringPersonName").value("Jane"));

        verify(domainService, times(1)).assignPerson(eq(1L), any());
        verify(domainService, times(1)).findById(eq(1L));
    }

    @Test
    void reserveGhostNet_domainService_NOT_FOUND_returns404() throws Exception {
        ReserveRequest request = new ReserveRequest("Jane");
        when(domainService.assignPerson(eq(1L), any())).thenReturn(OperationResult.NOT_FOUND);

        mockMvc.perform(patch("/api/ghostnets/1/reserve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        verify(domainService, times(1)).assignPerson(eq(1L), any());
    }

    @Test
    void recoverGhostNet_domainService_OK_returns200() throws Exception {
        RecoverRequest request = new RecoverRequest("done");
        when(domainService.markRecovered(eq(1L))).thenReturn(OperationResult.OK);
        GhostNetBusinessLayerModel updated = new GhostNetBusinessLayerModel();
        updated.setId(1L);
        updated.setStatus(NetStatusBusinessLayerEnum.RECOVERED);
        when(domainService.findById(eq(1L))).thenReturn(java.util.Optional.of(updated));

        mockMvc.perform(patch("/api/ghostnets/1/recover")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("RECOVERED"));

        verify(domainService, times(1)).markRecovered(eq(1L));
        verify(domainService, times(1)).findById(eq(1L));
    }

    @Test
    void recoverGhostNet_domainService_CONFLICT_returns409() throws Exception {
        RecoverRequest request = new RecoverRequest("done");
        when(domainService.markRecovered(eq(1L))).thenReturn(OperationResult.CONFLICT);

        mockMvc.perform(patch("/api/ghostnets/1/recover")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());

        verify(domainService, times(1)).markRecovered(eq(1L));
    }

    @Test
    void testGetAllGhostNets() throws Exception {
        when(service.findAll()).thenReturn(List.of(sampleNet));

        mockMvc.perform(get("/api/ghostnets"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].location").value("Sample Location"));

        verify(service).findAll();
    }

    @Test
    void testGetGhostNetById() throws Exception {
        when(service.findById(1L)).thenReturn(Optional.of(sampleNet));

        mockMvc.perform(get("/api/ghostnets/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.location").value("Sample Location"));

        verify(service).findById(1L);
    }

    @Test
    void testGetGhostNetByIdNotFound() throws Exception {
        when(service.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/ghostnets/999"))
                .andExpect(status().isNotFound());

        verify(service).findById(999L);
    }

    @Test
    void testCreateGhostNet() throws Exception {
        CreateGhostNetRequest request = new CreateGhostNetRequest();
        request.setLocation("Test Location");
        request.setSize(15.5);

        when(service.save(any())).thenReturn(sampleNet);

        mockMvc.perform(post("/api/ghostnets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L));

        verify(service).save(any());
    }

    @Test
    void testReserveGhostNet() throws Exception {
        ReserveRequest request = new ReserveRequest();
        request.setPersonName("Test Person");

        when(service.reserve(eq(1L), any())).thenReturn(OperationResult.OK);
        when(service.findById(1L)).thenReturn(Optional.of(sampleNet));

        mockMvc.perform(post("/api/ghostnets/1/reserve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(service).reserve(eq(1L), any());
    }

    @Test
    void testReserveGhostNetNotFound() throws Exception {
        ReserveRequest request = new ReserveRequest();
        request.setPersonName("Test Person");

        when(service.reserve(eq(999L), any())).thenReturn(OperationResult.NOT_FOUND);

        mockMvc.perform(post("/api/ghostnets/999/reserve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        verify(service).reserve(eq(999L), any());
    }

    @Test
    void testReserveGhostNetConflict() throws Exception {
        ReserveRequest request = new ReserveRequest();
        request.setPersonName("Test Person");

        when(service.reserve(eq(1L), any())).thenReturn(OperationResult.CONFLICT);

        mockMvc.perform(post("/api/ghostnets/1/reserve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());

        verify(service).reserve(eq(1L), any());
    }

    @Test
    void testRecoverGhostNet() throws Exception {
        RecoverRequest request = new RecoverRequest();

        when(service.recover(1L)).thenReturn(OperationResult.OK);
        when(service.findById(1L)).thenReturn(Optional.of(sampleNet));

        mockMvc.perform(post("/api/ghostnets/1/recover")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(service).recover(1L);
    }

    @Test
    void testRecoverGhostNetNotFound() throws Exception {
        RecoverRequest request = new RecoverRequest();

        when(service.recover(999L)).thenReturn(OperationResult.NOT_FOUND);

        mockMvc.perform(post("/api/ghostnets/999/recover")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        verify(service).recover(999L);
    }

    @Test
    void testRecoverGhostNetConflict() throws Exception {
        RecoverRequest request = new RecoverRequest();

        when(service.recover(1L)).thenReturn(OperationResult.CONFLICT);

        mockMvc.perform(post("/api/ghostnets/1/recover")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());

        verify(service).recover(1L);
    }
}
