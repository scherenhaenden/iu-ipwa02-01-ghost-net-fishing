package de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Services;

import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Mappers.AbandonedNetBusinessLayerMapper;
import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Models.AbandonedNetBusinessLayerModel;
import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Models.NetStatusBusinessLayerEnum;
import de.iu.project.iuipwa0201ghostnetfishing.DatabaseLayer.Models.AbandonedNetDataLayerModel;
import de.iu.project.iuipwa0201ghostnetfishing.DatabaseLayer.Models.NetStatusDataLayerEnum;
import de.iu.project.iuipwa0201ghostnetfishing.DatabaseLayer.Models.PersonDataLayerModel;
import de.iu.project.iuipwa0201ghostnetfishing.DatabaseLayer.Repositories.AbandonedNetDataLayerModelRepository;
import de.iu.project.iuipwa0201ghostnetfishing.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AbandonedNetBusinessLayerServiceTest {

    @Mock
    AbandonedNetDataLayerModelRepository repository;

    @Mock
    AbandonedNetBusinessLayerMapper mapper;

    @InjectMocks
    AbandonedNetBusinessLayerService service;

    @Test
    void getAllNetsNewestFirst_delegates_and_maps() {
        when(repository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(new AbandonedNetDataLayerModel(1L, "loc", 1.0, NetStatusDataLayerEnum.REPORTED, (PersonDataLayerModel) null)));
        when(mapper.toBusinessModelList(any())).thenReturn(List.of(new AbandonedNetBusinessLayerModel()));

        List<AbandonedNetBusinessLayerModel> result = service.getAllNetsNewestFirst();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(repository).findAllByOrderByCreatedAtDesc();
        verify(mapper).toBusinessModelList(any());
    }

    @Test
    void save_maps_and_returns_saved() {
        AbandonedNetBusinessLayerModel model = new AbandonedNetBusinessLayerModel();
        AbandonedNetDataLayerModel entity = new AbandonedNetDataLayerModel(2L, "loc", 1.0, NetStatusDataLayerEnum.REPORTED, (PersonDataLayerModel) null);
        AbandonedNetDataLayerModel saved = new AbandonedNetDataLayerModel(3L, "loc", 1.0, NetStatusDataLayerEnum.REPORTED, (PersonDataLayerModel) null);

        when(mapper.toEntity(model)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(saved);
        when(mapper.toBusinessModel(saved)).thenReturn(new AbandonedNetBusinessLayerModel());

        AbandonedNetBusinessLayerModel out = service.save(model);
        assertNotNull(out);
        verify(mapper).toEntity(model);
        verify(repository).save(entity);
        verify(mapper).toBusinessModel(saved);
    }

    @Test
    void findByStatus_converts_enum_and_maps() {
        when(repository.findByStatusOrderByCreatedAtDesc(NetStatusDataLayerEnum.REPORTED))
                .thenReturn(List.of(new AbandonedNetDataLayerModel(4L, "loc", 1.0, NetStatusDataLayerEnum.REPORTED, (PersonDataLayerModel) null)));
        when(mapper.toBusinessModelList(any())).thenReturn(List.of(new AbandonedNetBusinessLayerModel()));

        List<AbandonedNetBusinessLayerModel> out = service.findByStatus(NetStatusBusinessLayerEnum.REPORTED);
        assertNotNull(out);
        assertEquals(1, out.size());
        verify(repository).findByStatusOrderByCreatedAtDesc(NetStatusDataLayerEnum.REPORTED);
        verify(mapper).toBusinessModelList(any());
    }

    @Test
    void findById_returns_model_or_throws() {
        AbandonedNetDataLayerModel entity = new AbandonedNetDataLayerModel(5L, "loc", 1.0, NetStatusDataLayerEnum.REPORTED, (PersonDataLayerModel) null);
        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        when(mapper.toBusinessModel(entity)).thenReturn(new AbandonedNetBusinessLayerModel());

        AbandonedNetBusinessLayerModel out = service.findById(1L);
        assertNotNull(out);

        when(repository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.findById(2L));
    }

    @Test
    void deleteById_delegatesToRepository() {
        doNothing().when(repository).deleteById(10L);
        service.deleteById(10L);
        verify(repository).deleteById(10L);
    }
}
