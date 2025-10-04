package de.iu.project.iuipwa0201ghostnetfishing.web.controllers;

import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Models.GhostNetBusinessLayerModel;
import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Models.NetStatusBusinessLayerEnum;
import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Services.IGhostNetBusinessLayerService;
import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Services.OperationResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UiGhostNetController.class)
class UiGhostNetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IGhostNetBusinessLayerService service;

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
    void testListGhostNets() throws Exception {
        when(service.findAll()).thenReturn(List.of(sampleNet));

        mockMvc.perform(get("/ui/ghostnets"))
                .andExpect(status().isOk())
                .andExpect(view().name("ghostnets/list"))
                .andExpect(model().attributeExists("ghostNets"))
                .andExpect(model().attribute("selectedStatus", ""));

        verify(service).findAll();
    }

    @Test
    void testListGhostNetsWithStatus() throws Exception {
        when(service.findByStatus(NetStatusBusinessLayerEnum.REPORTED)).thenReturn(List.of(sampleNet));

        mockMvc.perform(get("/ui/ghostnets").param("status", "REPORTED"))
                .andExpect(status().isOk())
                .andExpect(view().name("ghostnets/list"))
                .andExpect(model().attributeExists("ghostNets"))
                .andExpect(model().attribute("selectedStatus", "REPORTED"));

        verify(service).findByStatus(NetStatusBusinessLayerEnum.REPORTED);
    }

    @Test
    void testCreateForm() throws Exception {
        mockMvc.perform(get("/ui/ghostnets/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("ghostnets/form-create"))
                .andExpect(model().attributeExists("ghostNetForm"));
    }

    @Test
    void testDetailView() throws Exception {
        when(service.findById(1L)).thenReturn(Optional.of(sampleNet));

        mockMvc.perform(get("/ui/ghostnets/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("ghostnets/detail"))
                .andExpect(model().attributeExists("ghostNet"));

        verify(service).findById(1L);
    }

    @Test
    void testDetailViewNotFound() throws Exception {
        when(service.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/ui/ghostnets/999"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/ui/ghostnets"))
                .andExpect(flash().attributeExists("error"));

        verify(service).findById(999L);
    }

    @Test
    void testReserveForm() throws Exception {
        when(service.findById(1L)).thenReturn(Optional.of(sampleNet));

        mockMvc.perform(get("/ui/ghostnets/1/reserve"))
                .andExpect(status().isOk())
                .andExpect(view().name("ghostnets/form-reserve"))
                .andExpect(model().attributeExists("ghostNet"))
                .andExpect(model().attributeExists("reserveForm"));

        verify(service).findById(1L);
    }

    @Test
    void testRecoverForm() throws Exception {
        when(service.findById(1L)).thenReturn(Optional.of(sampleNet));

        mockMvc.perform(get("/ui/ghostnets/1/recover"))
                .andExpect(status().isOk())
                .andExpect(view().name("ghostnets/form-recover"))
                .andExpect(model().attributeExists("ghostNet"))
                .andExpect(model().attributeExists("recoverForm"));

        verify(service).findById(1L);
    }

    @Test
    void testMissingForm() throws Exception {
        when(service.findById(1L)).thenReturn(Optional.of(sampleNet));

        mockMvc.perform(get("/ui/ghostnets/1/missing"))
                .andExpect(status().isOk())
                .andExpect(view().name("ghostnets/form-missing"))
                .andExpect(model().attributeExists("ghostNet"))
                .andExpect(model().attributeExists("missingForm"));

        verify(service).findById(1L);
    }

    @Test
    void testCreateGhostNet() throws Exception {
        when(service.save(any())).thenReturn(sampleNet);

        mockMvc.perform(post("/ui/ghostnets")
                .param("location", "Test Location")
                .param("size", "15.5")
                .param("personName", "Test Person"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/ui/ghostnets"))
                .andExpect(flash().attributeExists("ok"));

        verify(service).save(any());
    }

    @Test
    void testReserveNet() throws Exception {
        when(service.findById(1L)).thenReturn(Optional.of(sampleNet));
        when(service.reserve(eq(1L), any())).thenReturn(OperationResult.OK);

        mockMvc.perform(post("/ui/ghostnets/1/reserve")
                .param("personName", "Test Person"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/ui/ghostnets/1"))
                .andExpect(flash().attributeExists("ok"));

        verify(service).reserve(eq(1L), any());
    }

    @Test
    void testRecoverNet() throws Exception {
        when(service.findById(1L)).thenReturn(Optional.of(sampleNet));
        when(service.recover(1L)).thenReturn(OperationResult.OK);

        mockMvc.perform(post("/ui/ghostnets/1/recover")
                .param("notes", "Recovery notes"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/ui/ghostnets/1"))
                .andExpect(flash().attributeExists("ok"));

        verify(service).recover(1L);
    }

    @Test
    void testMarkNetMissing() throws Exception {
        when(service.findById(1L)).thenReturn(Optional.of(sampleNet));
        when(service.markMissing(1L)).thenReturn(OperationResult.OK);

        mockMvc.perform(post("/ui/ghostnets/1/missing")
                .param("notes", "Missing notes"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/ui/ghostnets/1"))
                .andExpect(flash().attributeExists("ok"));

        verify(service).markMissing(1L);
    }
}
