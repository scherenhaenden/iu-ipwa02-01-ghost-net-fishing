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
        sample.setLocation("L");
        sample.setSize(2.0);
        sample.setStatus(NetStatusBusinessLayerEnum.REPORTED);
        sample.setCreatedAt(Instant.now());
    }

    @Test
    void findAll_noStatus_returnsList() throws Exception {
        when(service.findAll()).thenReturn(List.of(sample));

        mockMvc.perform(get("/api/ghostnets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].location").value("L"));

        verify(service, times(1)).findAll();
    }

    @Test
    void findAll_withStatus_returnsFiltered() throws Exception {
        sample.setStatus(NetStatusBusinessLayerEnum.RECOVERED);
        when(service.findByStatus(NetStatusBusinessLayerEnum.RECOVERED)).thenReturn(List.of(sample));

        mockMvc.perform(get("/api/ghostnets").param("status", "recovered"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("RECOVERED"));

        verify(service, times(1)).findByStatus(NetStatusBusinessLayerEnum.RECOVERED);
    }

    @Test
    void findOne_returnsSingle() throws Exception {
        when(service.findByIdOrThrow(eq(1L))).thenReturn(sample);

        mockMvc.perform(get("/api/ghostnets/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(service, times(1)).findByIdOrThrow(eq(1L));
    }

    @Test
    void create_valid_returnsCreatedWithLocation() throws Exception {
        CreateGhostNetRequest req = new CreateGhostNetRequest("loc", 3.0, null);
        GhostNetBusinessLayerModel saved = new GhostNetBusinessLayerModel();
        saved.setId(42L);
        saved.setLocation("loc");
        saved.setSize(3.0);
        saved.setStatus(NetStatusBusinessLayerEnum.REPORTED);
        saved.setCreatedAt(Instant.now());
        when(service.save(any())).thenReturn(saved);

        mockMvc.perform(post("/api/ghostnets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", org.hamcrest.Matchers.containsString("/api/ghostnets/42")))
                .andExpect(jsonPath("$.id").value(42));

        verify(service, times(1)).save(any());
    }

    @Test
    void create_invalid_returnsValidationError() throws Exception {
        // missing location -> validation error
        CreateGhostNetRequest req = new CreateGhostNetRequest("", null, null);

        mockMvc.perform(post("/api/ghostnets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));

        verify(service, never()).save(any());
    }

    @Test
    void reserve_domainService_OK_maps200() throws Exception {
        ReserveRequest req = new ReserveRequest("Alice");
        PersonBusinessLayerModel p = new PersonBusinessLayerModel();
        p.setName("Alice");
        GhostNetBusinessLayerModel updated = new GhostNetBusinessLayerModel();
        updated.setId(1L);
        updated.setStatus(NetStatusBusinessLayerEnum.RECOVERY_PENDING);
        updated.setRecoveringPerson(p);

        when(domainService.assignPerson(eq(1L), any())).thenReturn(OperationResult.OK);
        when(domainService.findById(eq(1L))).thenReturn(Optional.of(updated));

        mockMvc.perform(patch("/api/ghostnets/1/reserve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recoveringPersonName").value("Alice"));

        verify(domainService, times(1)).assignPerson(eq(1L), any());
    }

    @Test
    void reserve_serviceFallback_assignsAndReturns200_whenServiceReserveNull() throws Exception {
        ReserveRequest req = new ReserveRequest("Bob");
        PersonBusinessLayerModel p = new PersonBusinessLayerModel();
        p.setName("Bob");
        // domainService returns null to force fallback
        when(domainService.assignPerson(eq(1L), any())).thenReturn(null);
        when(service.reserve(eq(1L), any())).thenReturn(null);
        when(service.findByIdOrThrow(eq(1L))).thenReturn(sample);
        GhostNetBusinessLayerModel saved = new GhostNetBusinessLayerModel();
        saved.setId(1L);
        saved.setStatus(NetStatusBusinessLayerEnum.RECOVERY_PENDING);
        PersonBusinessLayerModel rp = new PersonBusinessLayerModel(); rp.setName("Bob");
        saved.setRecoveringPerson(rp);
        when(service.save(any())).thenReturn(saved);

        mockMvc.perform(patch("/api/ghostnets/1/reserve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("RECOVERY_PENDING"))
                .andExpect(jsonPath("$.recoveringPersonName").value("Bob"));

        verify(service, times(1)).findByIdOrThrow(eq(1L));
        verify(service, times(1)).save(any());
    }

    @Test
    void reserve_domainService_CONFLICT_maps409() throws Exception {
        ReserveRequest req = new ReserveRequest("X");
        when(domainService.assignPerson(eq(1L), any())).thenReturn(OperationResult.CONFLICT);

        mockMvc.perform(patch("/api/ghostnets/1/reserve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isConflict());

        verify(domainService, times(1)).assignPerson(eq(1L), any());
    }

    @Test
    void reserve_missingPerson_validation_shouldReturn400() throws Exception {
        // empty personName -> validation error
        ReserveRequest req = new ReserveRequest("");

        mockMvc.perform(patch("/api/ghostnets/1/reserve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));

        verify(domainService, never()).assignPerson(any(), any());
    }

    @Test
    void recover_domainService_OK_maps200() throws Exception {
        when(domainService.markRecovered(eq(1L))).thenReturn(OperationResult.OK);
        GhostNetBusinessLayerModel updated = new GhostNetBusinessLayerModel();
        updated.setId(1L);
        updated.setStatus(NetStatusBusinessLayerEnum.RECOVERED);
        when(domainService.findById(eq(1L))).thenReturn(Optional.of(updated));

        mockMvc.perform(patch("/api/ghostnets/1/recover")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new RecoverRequest("ok"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("RECOVERED"));

        verify(domainService, times(1)).markRecovered(eq(1L));
    }

    @Test
    void recover_serviceFallback_marksRecoveredWhenDomainNull() throws Exception {
        when(domainService.markRecovered(eq(1L))).thenReturn(null);
        sample.setStatus(NetStatusBusinessLayerEnum.RECOVERY_PENDING);
        when(service.findByIdOrThrow(eq(1L))).thenReturn(sample);
        GhostNetBusinessLayerModel saved = new GhostNetBusinessLayerModel();
        saved.setId(1L);
        saved.setStatus(NetStatusBusinessLayerEnum.RECOVERED);
        when(service.save(any())).thenReturn(saved);

        mockMvc.perform(patch("/api/ghostnets/1/recover")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new RecoverRequest("notes"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("RECOVERED"));

        verify(service, times(1)).findByIdOrThrow(eq(1L));
        verify(service, times(1)).save(any());
    }

}

