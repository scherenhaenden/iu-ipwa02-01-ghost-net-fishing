package de.iu.project.iuipwa0201ghostnetfishing.web.controllers;

import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Models.GhostNetBusinessLayerModel;
import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Models.NetStatusBusinessLayerEnum;
import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Models.PersonBusinessLayerModel;
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
import java.util.Collections;
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
    void list_returnsViewWithModel() throws Exception {
        when(service.findAll()).thenReturn(List.of(sampleNet));

        mockMvc.perform(get("/ui/ghostnets"))
                .andExpect(status().isOk())
                .andExpect(view().name("ghostnets/list"))
                .andExpect(model().attributeExists("ghostNets"));

        verify(service, times(1)).findAll();
    }

    @Test
    void list_with_invalid_status_returnsEmptyList() throws Exception {
        mockMvc.perform(get("/ui/ghostnets").param("status", "invalid"))
                .andExpect(status().isOk())
                .andExpect(view().name("ghostnets/list"))
                .andExpect(model().attribute("ghostNets", Collections.emptyList()));
    }

    @Test
    void createForm_returnsCreateView() throws Exception {
        mockMvc.perform(get("/ui/ghostnets/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("ghostnets/form-create"))
                .andExpect(model().attributeExists("ghostNetForm"));
    }

    @Test
    void create_post_valid_callsServiceAndRedirects() throws Exception {
        // service.save is called with a GhostNetBusinessLayerModel
        when(service.save(any())).thenReturn(sampleNet);

        mockMvc.perform(post("/ui/ghostnets")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("location", "L")
                .param("size", "1.0")
                .param("personName", "")
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/ui/ghostnets"));

        verify(service, times(1)).save(any());
    }

    @Test
    void create_post_invalid_showsForm() throws Exception {
        // missing size -> validation error
        mockMvc.perform(post("/ui/ghostnets")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("location", "")
        )
                .andExpect(status().isOk())
                .andExpect(view().name("ghostnets/form-create"));

        verify(service, never()).save(any());
    }

    @Test
    void reserveForm_found_returnsView() throws Exception {
        when(service.findById(eq(1L))).thenReturn(Optional.of(sampleNet));

        mockMvc.perform(get("/ui/ghostnets/1/reserve"))
                .andExpect(status().isOk())
                .andExpect(view().name("ghostnets/form-reserve"))
                .andExpect(model().attributeExists("reserveForm"))
                .andExpect(model().attributeExists("ghostNet"));

        verify(service, times(1)).findById(eq(1L));
    }

    @Test
    void reserveForm_notFound_redirectsWithFlash() throws Exception {
        when(service.findById(eq(1L))).thenReturn(Optional.empty());

        mockMvc.perform(get("/ui/ghostnets/1/reserve"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/ui/ghostnets"))
                .andExpect(flash().attribute("error", "Ghost net nicht gefunden."));

        verify(service, times(1)).findById(eq(1L));
    }

    @Test
    void reserve_post_bindingErrors_reRendersForm() throws Exception {
        when(service.findById(eq(1L))).thenReturn(Optional.of(sampleNet));

        mockMvc.perform(post("/ui/ghostnets/1/reserve")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("personName", "")
        )
                .andExpect(status().isOk())
                .andExpect(view().name("ghostnets/form-reserve"))
                .andExpect(model().attributeExists("reserveForm"))
                .andExpect(model().attributeExists("ghostNet"));

        verify(service, never()).reserve(eq(1L), any());
        verify(service, times(1)).findById(eq(1L));
    }

    @Test
    void reserve_post_domainService_OK_mapsRedirectToDetail() throws Exception {
        // Arrange
        PersonBusinessLayerModel p = new PersonBusinessLayerModel();
        p.setName("Jane");

        GhostNetBusinessLayerModel updated = new GhostNetBusinessLayerModel();
        updated.setId(1L);
        updated.setStatus(NetStatusBusinessLayerEnum.RECOVERY_PENDING);
        updated.setRecoveringPerson(p);

        when(service.reserve(eq(1L), any())).thenReturn(OperationResult.OK);
        when(service.findById(eq(1L))).thenReturn(Optional.of(updated));

        mockMvc.perform(post("/ui/ghostnets/1/reserve")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("personName", "Jane")
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/ui/ghostnets/1"));

        verify(service, times(1)).reserve(eq(1L), any());
        // controller invokes findById twice in this flow (initial opt + fetch updated)
        verify(service, times(2)).findById(eq(1L));
    }

    @Test
    void reserve_post_domainService_CONFLICT_showsRedirectToDetailWithError() throws Exception {
        GhostNetBusinessLayerModel current = new GhostNetBusinessLayerModel();
        current.setId(1L);
        current.setStatus(NetStatusBusinessLayerEnum.RECOVERY_PENDING);

        when(service.reserve(eq(1L), any())).thenReturn(OperationResult.CONFLICT);
        when(service.findById(eq(1L))).thenReturn(Optional.of(current));

        mockMvc.perform(post("/ui/ghostnets/1/reserve")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("personName", "Joe")
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/ui/ghostnets/1"));

        verify(service, times(1)).reserve(eq(1L), any());
        // conflict branch also calls findById twice (initial opt + current)
        verify(service, times(2)).findById(eq(1L));
    }

    @Test
    void recoverForm_notFound_redirectsWithFlash() throws Exception {
        when(service.findById(eq(1L))).thenReturn(Optional.empty());

        mockMvc.perform(get("/ui/ghostnets/1/recover"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/ui/ghostnets"))
                .andExpect(flash().attribute("error", "Ghost net nicht gefunden."));

        verify(service, times(1)).findById(eq(1L));
    }

    @Test
    void recover_post_serviceNull_redirectsWithFlash() throws Exception {
        when(service.findById(eq(1L))).thenReturn(Optional.of(sampleNet));
        when(service.recover(eq(1L))).thenReturn(null);

        mockMvc.perform(post("/ui/ghostnets/1/recover")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("notes", "done")
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/ui/ghostnets"))
                .andExpect(flash().attribute("error", "Unable to mark net as recovered (internal)."));

        verify(service, times(1)).recover(eq(1L));
        verify(service, times(1)).findById(eq(1L));
    }

    @Test
    void missing_get_and_post_flows() throws Exception {
        // GET form
        when(service.findById(eq(1L))).thenReturn(Optional.of(sampleNet));

        mockMvc.perform(get("/ui/ghostnets/1/missing"))
                .andExpect(status().isOk())
                .andExpect(view().name("ghostnets/form-missing"))
                .andExpect(model().attributeExists("missingForm"))
                .andExpect(model().attributeExists("ghostNet"));

        verify(service, times(1)).findById(eq(1L));

        // POST markMissing OK
        GhostNetBusinessLayerModel updated = new GhostNetBusinessLayerModel();
        updated.setId(1L);
        updated.setStatus(NetStatusBusinessLayerEnum.RECOVERED); // any status

        when(service.markMissing(eq(1L))).thenReturn(OperationResult.OK);
        when(service.findById(eq(1L))).thenReturn(Optional.of(updated));

        mockMvc.perform(post("/ui/ghostnets/1/missing")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("notes", "lost")
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/ui/ghostnets/1"));

        verify(service, times(1)).markMissing(eq(1L));
        verify(service, atLeastOnce()).findById(eq(1L));

        // POST markMissing null -> internal error path
        when(service.markMissing(eq(1L))).thenReturn(null);
        when(service.findById(eq(1L))).thenReturn(Optional.of(sampleNet));

        mockMvc.perform(post("/ui/ghostnets/1/missing")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("notes", "lost")
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/ui/ghostnets"))
                .andExpect(flash().attribute("error", "Unable to mark net as missing (internal)."));

        verify(service, times(2)).markMissing(eq(1L));
    }

    @Test
    void detail_notFound_redirectsWithFlash() throws Exception {
        when(service.findById(eq(1L))).thenReturn(Optional.empty());

        mockMvc.perform(get("/ui/ghostnets/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/ui/ghostnets"))
                .andExpect(flash().attribute("error", "Ghost net nicht gefunden."));

        verify(service, times(1)).findById(eq(1L));
    }

}
