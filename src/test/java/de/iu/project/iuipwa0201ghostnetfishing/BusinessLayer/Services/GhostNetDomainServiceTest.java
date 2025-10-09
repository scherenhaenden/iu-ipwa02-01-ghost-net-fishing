// ...existing code...
package de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Services;

import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Mappers.GhostNetBusinessLayerMapper;
import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Mappers.PersonBusinessLayerMapper;
import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Models.GhostNetBusinessLayerModel;
import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Models.NetStatusBusinessLayerEnum;
import de.iu.project.iuipwa0201ghostnetfishing.DatabaseLayer.Models.GhostNetDataLayerModel;
import de.iu.project.iuipwa0201ghostnetfishing.DatabaseLayer.Models.NetStatusDataLayerEnum;
import de.iu.project.iuipwa0201ghostnetfishing.DatabaseLayer.Repositories.GhostNetDataLayerModelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GhostNetDomainServiceTest {

    @Mock
    private GhostNetDataLayerModelRepository repository;

    @Mock
    private GhostNetBusinessLayerMapper mapper;

    @Mock
    private PersonBusinessLayerMapper personMapper;

    private GhostNetDomainService service;

    @BeforeEach
    void setUp() {
        service = new GhostNetDomainService(repository, mapper, personMapper);
    }

    @Captor
    ArgumentCaptor<GhostNetBusinessLayerModel> businessCaptor;

    @Test
    void save_assignsStatusAndCreatedAt_whenMissing() {
        GhostNetBusinessLayerModel input = new GhostNetBusinessLayerModel();
        input.setLocation("Loc");
        input.setSize(2.0);
        // status and createdAt intentionally null

        GhostNetDataLayerModel mockedEntity = mock(GhostNetDataLayerModel.class);
        when(mapper.toEntity(any())).thenReturn(mockedEntity);
        when(repository.save(mockedEntity)).thenReturn(mockedEntity);
        when(mapper.toBusinessModel(mockedEntity)).thenReturn(input);

        service.save(input);

        // capture the business model passed to mapper.toEntity and assert defaults applied
        verify(mapper).toEntity(businessCaptor.capture());
        GhostNetBusinessLayerModel captured = businessCaptor.getValue();
        assertNotNull(captured.getStatus(), "status should be set");
        assertEquals(NetStatusBusinessLayerEnum.REPORTED, captured.getStatus());
        assertNotNull(captured.getCreatedAt(), "createdAt should be set to now");
    }

    @Test
    void findAll_callsRepositoryMethods_and_respectsFilter() {
        // prepare mocks for no-status case
        GhostNetDataLayerModel e1 = mock(GhostNetDataLayerModel.class);
        GhostNetDataLayerModel e2 = mock(GhostNetDataLayerModel.class);
        List<GhostNetDataLayerModel> ents = List.of(e1, e2);
        GhostNetBusinessLayerModel b1 = new GhostNetBusinessLayerModel();
        GhostNetBusinessLayerModel b2 = new GhostNetBusinessLayerModel();
        List<GhostNetBusinessLayerModel> bms = List.of(b1, b2);

        when(repository.findAllByOrderByCreatedAtDesc()).thenReturn(ents);
        when(mapper.toBusinessModelList(ents)).thenReturn(bms);

        List<GhostNetBusinessLayerModel> result = service.findAll(Optional.empty());
        assertThat(result).isEqualTo(bms);
        verify(repository, times(1)).findAllByOrderByCreatedAtDesc();
        verify(repository, never()).findByStatusOrderByCreatedAtDesc(any());

        // now test with status filter
        when(repository.findByStatusOrderByCreatedAtDesc(NetStatusDataLayerEnum.REPORTED)).thenReturn(ents);
        when(mapper.toBusinessModelList(ents)).thenReturn(bms);

        List<GhostNetBusinessLayerModel> filtered = service.findAll(Optional.of(NetStatusBusinessLayerEnum.REPORTED));
        assertThat(filtered).isEqualTo(bms);
        verify(repository, times(1)).findByStatusOrderByCreatedAtDesc(NetStatusDataLayerEnum.REPORTED);
    }
}

