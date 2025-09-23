package de.iu.project.iuipwa0201ghostnetfishing.web.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Models.GhostNetBusinessLayerModel;
import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Models.NetStatusBusinessLayerEnum;
import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Models.PersonBusinessLayerModel;
import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Services.IGhostNetBusinessLayerService;
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
}
