package de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Services;

import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Mappers.GhostNetBusinessLayerMapper;
import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Mappers.PersonBusinessLayerMapper;
import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Models.GhostNetBusinessLayerModel;
import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Models.NetStatusBusinessLayerEnum;
import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Models.PersonBusinessLayerModel;
import de.iu.project.iuipwa0201ghostnetfishing.DatabaseLayer.Models.GhostNetDataLayerModel;
import de.iu.project.iuipwa0201ghostnetfishing.DatabaseLayer.Models.NetStatusDataLayerEnum;
import de.iu.project.iuipwa0201ghostnetfishing.DatabaseLayer.Models.PersonDataLayerModel;
import de.iu.project.iuipwa0201ghostnetfishing.DatabaseLayer.Repositories.GhostNetDataLayerModelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GhostNetBusinessLayerServiceTest {

    @Mock
    GhostNetDataLayerModelRepository repository;

    @Mock
    GhostNetBusinessLayerMapper mapper;

    @Mock
    PersonBusinessLayerMapper personMapper;

    @InjectMocks
    GhostNetBusinessLayerService service;

    @Captor
    ArgumentCaptor<GhostNetDataLayerModel> entityCaptor;

    @BeforeEach
    void setup() {
        // Mockito will inject mocks into the service instance
    }

    @Test
    void findAll_delegatesToRepositoryAndMapper() {
        // use public constructor instead of protected no-arg
        when(repository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(new GhostNetDataLayerModel(1L, "loc", 1.0, NetStatusDataLayerEnum.REPORTED, (PersonDataLayerModel) null)));
        when(mapper.toBusinessModelList(any())).thenReturn(List.of(new GhostNetBusinessLayerModel()));

        List<GhostNetBusinessLayerModel> result = service.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(repository).findAllByOrderByCreatedAtDesc();
        verify(mapper).toBusinessModelList(any());
    }

    @Test
    void save_null_returnsNull() {
        assertNull(service.save(null));
        verifyNoInteractions(repository, mapper);
    }

    @Test
    void reserve_handles_nullId_and_nullPerson() {
        PersonBusinessLayerModel person = new PersonBusinessLayerModel();
        assertEquals(OperationResult.NOT_FOUND, service.reserve(null, person));
        assertEquals(OperationResult.BAD_REQUEST, service.reserve(1L, null));
    }

    @Test
    void reserve_reported_changesStateAndSaves() {
        long id = 5L;
        GhostNetDataLayerModel entity = new GhostNetDataLayerModel(id, "loc", 2.0, NetStatusDataLayerEnum.REPORTED, (PersonDataLayerModel) null);
        entity.setPerson(null);

        PersonBusinessLayerModel person = new PersonBusinessLayerModel();
        person.setName("John");

        PersonDataLayerModel personEntity = new PersonDataLayerModel(7L, "John", null);

        when(repository.findById(id)).thenReturn(Optional.of(entity));
        when(personMapper.toEntity(person)).thenReturn(personEntity);
        when(repository.save(any())).thenReturn(entity);

        OperationResult res = service.reserve(id, person);

        assertEquals(OperationResult.OK, res);
        verify(repository).save(entityCaptor.capture());
        GhostNetDataLayerModel saved = entityCaptor.getValue();
        assertEquals(NetStatusDataLayerEnum.RECOVERY_PENDING, saved.getStatus());
        assertNotNull(saved.getPerson());
        assertEquals("John", saved.getPerson().getName());
    }

    @Test
    void reserve_recovers_conflict_onDifferentPerson() {
        long id = 6L;
        GhostNetDataLayerModel entity = new GhostNetDataLayerModel(id, "loc", 2.0, NetStatusDataLayerEnum.RECOVERY_PENDING, new PersonDataLayerModel(2L, "Alice", null));

        PersonBusinessLayerModel other = new PersonBusinessLayerModel();
        other.setName("Bob");

        when(repository.findById(id)).thenReturn(Optional.of(entity));

        OperationResult res = service.reserve(id, other);

        assertEquals(OperationResult.CONFLICT, res);
    }

    @Test
    void recover_allows_recovery_when_pending_and_idempotent_when_recovered() {
        long id = 7L;
        GhostNetDataLayerModel pending = new GhostNetDataLayerModel(id, "loc", 1.0, NetStatusDataLayerEnum.RECOVERY_PENDING, (PersonDataLayerModel) null);

        when(repository.findById(id)).thenReturn(Optional.of(pending));
        when(repository.save(any())).thenReturn(pending);

        assertEquals(OperationResult.OK, service.recover(id));
        verify(repository).save(any());

        // already recovered
        GhostNetDataLayerModel recovered = new GhostNetDataLayerModel(id, "loc", 1.0, NetStatusDataLayerEnum.RECOVERED, (PersonDataLayerModel) null);
        when(repository.findById(id)).thenReturn(Optional.of(recovered));

        assertEquals(OperationResult.OK, service.recover(id));
    }

    @Test
    void recover_conflict_from_wrong_state() {
        long id = 8L;
        GhostNetDataLayerModel entity = new GhostNetDataLayerModel(id, "loc", 1.0, NetStatusDataLayerEnum.REPORTED, (PersonDataLayerModel) null);
        when(repository.findById(id)).thenReturn(Optional.of(entity));

        assertEquals(OperationResult.CONFLICT, service.recover(id));
    }

    @Test
    void markMissing_behaviour() {
        long id = 9L;
        GhostNetDataLayerModel reported = new GhostNetDataLayerModel(id, "loc", 1.0, NetStatusDataLayerEnum.REPORTED, (PersonDataLayerModel) null);
        when(repository.findById(id)).thenReturn(Optional.of(reported));
        when(repository.save(any())).thenReturn(reported);

        assertEquals(OperationResult.OK, service.markMissing(id));
        verify(repository).save(any());

        // cannot mark missing from recovered
        GhostNetDataLayerModel recovered = new GhostNetDataLayerModel(id, "loc", 1.0, NetStatusDataLayerEnum.RECOVERED, (PersonDataLayerModel) null);
        when(repository.findById(id)).thenReturn(Optional.of(recovered));

        assertEquals(OperationResult.CONFLICT, service.markMissing(id));
    }
}
