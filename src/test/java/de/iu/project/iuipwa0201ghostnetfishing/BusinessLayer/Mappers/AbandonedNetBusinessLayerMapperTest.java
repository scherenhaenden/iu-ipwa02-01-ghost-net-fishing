package de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Mappers;

import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Models.AbandonedNetBusinessLayerModel;
import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Models.NetStatusBusinessLayerEnum;
import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Models.PersonBusinessLayerModel;
import de.iu.project.iuipwa0201ghostnetfishing.DatabaseLayer.Models.AbandonedNetDataLayerModel;
import de.iu.project.iuipwa0201ghostnetfishing.DatabaseLayer.Models.NetStatusDataLayerEnum;
import de.iu.project.iuipwa0201ghostnetfishing.DatabaseLayer.Models.PersonDataLayerModel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AbandonedNetBusinessLayerMapperTest {

    @Mock
    private PersonBusinessLayerMapper personMapper;

    @InjectMocks
    private AbandonedNetBusinessLayerMapper mapper;

    @BeforeEach
    void setUp() {
        // Mockito injects automatically
    }

    @Test
    void toBusinessModel_ShouldMapEntityToBusinessModel() {
        // Given
        PersonDataLayerModel personEntity = new PersonDataLayerModel();
        personEntity.setId(1L);
        personEntity.setName("John Doe");

        AbandonedNetDataLayerModel entity = new AbandonedNetDataLayerModel(1L, "Test Location", 10.5, NetStatusDataLayerEnum.REPORTED, personEntity);
        Date createdAtDate = Date.from(Instant.now());
        entity.setCreatedAt(createdAtDate);

        PersonBusinessLayerModel mockedPersonModel = new PersonBusinessLayerModel();
        mockedPersonModel.setId(1L);
        mockedPersonModel.setName("John Doe");
        when(personMapper.toBusiness(any(PersonDataLayerModel.class))).thenReturn(mockedPersonModel);

        // When
        AbandonedNetBusinessLayerModel result = mapper.toBusinessModel(entity);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Location", result.getLocation());
        assertEquals(10.5, result.getSize());
        assertEquals(NetStatusBusinessLayerEnum.REPORTED, result.getStatus());
        assertEquals(createdAtDate.toInstant(), result.getCreatedAt());
        assertEquals(mockedPersonModel, result.getPerson());
        verify(personMapper).toBusiness(any(PersonDataLayerModel.class));
    }

    @Test
    void toBusinessModel_ShouldReturnNull_WhenEntityIsNull() {
        // When
        AbandonedNetBusinessLayerModel result = mapper.toBusinessModel(null);

        // Then
        assertNull(result);
        verifyNoInteractions(personMapper);
    }

    @Test
    void toBusinessModelList_ShouldMapListOfEntitiesToListOfBusinessModels() {
        // Given
        PersonDataLayerModel person1 = new PersonDataLayerModel();
        person1.setId(1L);
        person1.setName("John Doe 1");
        PersonDataLayerModel person2 = new PersonDataLayerModel();
        person2.setId(2L);
        person2.setName("John Doe 2");

        AbandonedNetDataLayerModel entity1 = new AbandonedNetDataLayerModel(1L, "Location 1", 1.0, NetStatusDataLayerEnum.REPORTED, person1);
        AbandonedNetDataLayerModel entity2 = new AbandonedNetDataLayerModel(2L, "Location 2", 2.0, NetStatusDataLayerEnum.REPORTED, person2);

        List<AbandonedNetDataLayerModel> entities = Arrays.asList(entity1, entity2);

        PersonBusinessLayerModel mockedPerson1 = new PersonBusinessLayerModel();
        mockedPerson1.setId(1L);
        mockedPerson1.setName("John Doe 1");
        PersonBusinessLayerModel mockedPerson2 = new PersonBusinessLayerModel();
        mockedPerson2.setId(2L);
        mockedPerson2.setName("John Doe 2");
        when(personMapper.toBusiness(person1)).thenReturn(mockedPerson1);
        when(personMapper.toBusiness(person2)).thenReturn(mockedPerson2);

        // When
        List<AbandonedNetBusinessLayerModel> result = mapper.toBusinessModelList(entities);

        // Then
        assertEquals(2, result.size());
        assertEquals("Location 1", result.get(0).getLocation());
        assertEquals("Location 2", result.get(1).getLocation());
        verify(personMapper).toBusiness(person1);
        verify(personMapper).toBusiness(person2);
    }

    @Test
    void toBusinessModelList_ShouldReturnEmptyList_WhenEntitiesIsNull() {
        // When
        List<AbandonedNetBusinessLayerModel> result = mapper.toBusinessModelList(null);

        // Then
        assertTrue(result.isEmpty());
        verifyNoInteractions(personMapper);
    }

    @Test
    void toEntity_ShouldMapBusinessModelToEntity() {
        // Given
        AbandonedNetBusinessLayerModel model = new AbandonedNetBusinessLayerModel();
        model.setId(1L);
        model.setLocation("Test Location");
        model.setSize(10.5);
        model.setStatus(NetStatusBusinessLayerEnum.REPORTED);
        Instant createdAtInstant = Instant.now();
        model.setCreatedAt(createdAtInstant);

        PersonBusinessLayerModel personModel = new PersonBusinessLayerModel();
        personModel.setId(1L);
        model.setPerson(personModel);

        PersonDataLayerModel mockedPersonEntity = new PersonDataLayerModel();
        mockedPersonEntity.setId(1L);
        when(personMapper.toEntity(any(PersonBusinessLayerModel.class))).thenReturn(mockedPersonEntity);

        // When
        AbandonedNetDataLayerModel result = mapper.toEntity(model);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Location", result.getLocation());
        assertEquals(10.5, result.getSize());
        assertEquals(NetStatusDataLayerEnum.REPORTED, result.getStatus());
        assertEquals(Date.from(createdAtInstant), result.getCreatedAt());
        assertEquals(mockedPersonEntity, result.getPerson());
        verify(personMapper).toEntity(any(PersonBusinessLayerModel.class));
    }

    @Test
    void toEntity_ShouldReturnNull_WhenModelIsNull() {
        // When
        AbandonedNetDataLayerModel result = mapper.toEntity(null);

        // Then
        assertNull(result);
        verifyNoInteractions(personMapper);
    }

    @Test
    void toEntity_ShouldNotSetCreatedAt_WhenModelCreatedAtIsNull() {
        // Given
        AbandonedNetBusinessLayerModel model = new AbandonedNetBusinessLayerModel();
        model.setId(1L);
        PersonBusinessLayerModel personModel = new PersonBusinessLayerModel();
        model.setPerson(personModel);

        when(personMapper.toEntity(any(PersonBusinessLayerModel.class))).thenReturn(new PersonDataLayerModel());

        // When
        AbandonedNetDataLayerModel result = mapper.toEntity(model);

        // Then
        assertNull(result.getCreatedAt());
        verify(personMapper).toEntity(any(PersonBusinessLayerModel.class));
    }
}
