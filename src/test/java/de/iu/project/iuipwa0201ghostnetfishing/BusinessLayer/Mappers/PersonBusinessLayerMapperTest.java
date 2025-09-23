package de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Mappers;

import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Models.PersonBusinessLayerModel;
import de.iu.project.iuipwa0201ghostnetfishing.DatabaseLayer.Models.PersonDataLayerModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PersonBusinessLayerMapperTest {

    private PersonBusinessLayerMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new PersonBusinessLayerMapper();
    }

    @Test
    void toBusiness_ShouldMapEntityToBusinessModel() {
        // Given
        PersonDataLayerModel entity = new PersonDataLayerModel();
        entity.setId(1L);
        entity.setName("John Doe");
        entity.setPhoneNumber("123-456-7890");

        // When
        PersonBusinessLayerModel result = mapper.toBusiness(entity);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("John Doe", result.getName());
        assertEquals("123-456-7890", result.getPhoneNumber());
    }

    @Test
    void toBusiness_ShouldReturnNull_WhenEntityIsNull() {
        // When
        PersonBusinessLayerModel result = mapper.toBusiness(null);

        // Then
        assertNull(result);
    }

    @Test
    void toEntity_ShouldMapBusinessModelToEntity() {
        // Given
        PersonBusinessLayerModel model = new PersonBusinessLayerModel();
        model.setId(1L);
        model.setName("John Doe");
        model.setPhoneNumber("123-456-7890");

        // When
        PersonDataLayerModel result = mapper.toEntity(model);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("John Doe", result.getName());
        assertEquals("123-456-7890", result.getPhoneNumber());
    }

    @Test
    void toEntity_ShouldReturnNull_WhenModelIsNull() {
        // When
        PersonDataLayerModel result = mapper.toEntity(null);

        // Then
        assertNull(result);
    }

    @Test
    void toBusinessList_ShouldMapListOfEntitiesToListOfBusinessModels() {
        // Given
        PersonDataLayerModel entity1 = new PersonDataLayerModel();
        entity1.setId(1L);
        entity1.setName("John Doe");
        entity1.setPhoneNumber("123-456-7890");

        PersonDataLayerModel entity2 = new PersonDataLayerModel();
        entity2.setId(2L);
        entity2.setName("Jane Smith");
        entity2.setPhoneNumber("098-765-4321");

        List<PersonDataLayerModel> entities = Arrays.asList(entity1, entity2);

        // When
        List<PersonBusinessLayerModel> result = mapper.toBusinessList(entities);

        // Then
        assertEquals(2, result.size());
        assertEquals("John Doe", result.get(0).getName());
        assertEquals("Jane Smith", result.get(1).getName());
    }

    @Test
    void toBusinessList_ShouldReturnEmptyList_WhenEntitiesIsNull() {
        // When
        List<PersonBusinessLayerModel> result = mapper.toBusinessList(null);

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    void toEntityList_ShouldMapListOfBusinessModelsToListOfEntities() {
        // Given
        PersonBusinessLayerModel model1 = new PersonBusinessLayerModel();
        model1.setId(1L);
        model1.setName("John Doe");
        model1.setPhoneNumber("123-456-7890");

        PersonBusinessLayerModel model2 = new PersonBusinessLayerModel();
        model2.setId(2L);
        model2.setName("Jane Smith");
        model2.setPhoneNumber("098-765-4321");

        List<PersonBusinessLayerModel> models = Arrays.asList(model1, model2);

        // When
        List<PersonDataLayerModel> result = mapper.toEntityList(models);

        // Then
        assertEquals(2, result.size());
        assertEquals("John Doe", result.get(0).getName());
        assertEquals("Jane Smith", result.get(1).getName());
    }

    @Test
    void toEntityList_ShouldReturnEmptyList_WhenListIsNull() {
        // When
        List<PersonDataLayerModel> result = mapper.toEntityList(null);

        // Then
        assertTrue(result.isEmpty());
    }
}
