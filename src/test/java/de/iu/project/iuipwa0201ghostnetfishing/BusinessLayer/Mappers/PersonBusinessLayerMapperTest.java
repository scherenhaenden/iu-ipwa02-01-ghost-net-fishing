package de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Mappers;

import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Models.PersonBusinessLayerModel;
import de.iu.project.iuipwa0201ghostnetfishing.DatabaseLayer.Models.PersonDataLayerModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit-Tests für die PersonBusinessLayerMapper-Klasse.
 * Testet das Mapping zwischen PersonDataLayerModel und PersonBusinessLayerModel.
 */
class PersonBusinessLayerMapperTest {

    private PersonDataLayerModel entity;
    private PersonBusinessLayerModel model;

    @BeforeEach
    void setUp() {
        entity = new PersonDataLayerModel();
        entity.setId(1L);
        entity.setName("Test Person");
        entity.setPhoneNumber("+123456789");

        model = new PersonBusinessLayerModel();
        model.setId(1L);
        model.setName("Test Person");
        model.setPhoneNumber("+123456789");
    }

    @Test
    @DisplayName("toBusinessModel: Mapping von Entity zu BusinessModel")
    void testToBusinessModel() {
        // Act
        PersonBusinessLayerModel result = PersonBusinessLayerMapper.toBusinessModel(entity);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Person", result.getName());
        assertEquals("+123456789", result.getPhoneNumber());
    }

    @Test
    @DisplayName("toBusinessModel: Null-Entity gibt Null zurück")
    void testToBusinessModelNull() {
        // Act
        PersonBusinessLayerModel result = PersonBusinessLayerMapper.toBusinessModel(null);

        // Assert
        assertNull(result);
    }

    @Test
    @DisplayName("toBusinessModelList: Mapping einer Liste von Entities")
    void testToBusinessModelList() {
        // Arrange
        List<PersonDataLayerModel> entities = Arrays.asList(entity, createAnotherEntity());

        // Act
        List<PersonBusinessLayerModel> result = PersonBusinessLayerMapper.toBusinessModelList(entities);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals("Test Person", result.get(0).getName());
        assertEquals("+123456789", result.get(0).getPhoneNumber());
        assertEquals(2L, result.get(1).getId());
        assertEquals("Another Person", result.get(1).getName());
        assertEquals("+987654321", result.get(1).getPhoneNumber());
    }

    @Test
    @DisplayName("toBusinessModelList: Leere Liste gibt leere Liste zurück")
    void testToBusinessModelListEmpty() {
        // Act
        List<PersonBusinessLayerModel> result = PersonBusinessLayerMapper.toBusinessModelList(Collections.emptyList());

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("toBusinessModelList: Null-Liste gibt leere Liste zurück")
    void testToBusinessModelListNull() {
        // Act
        List<PersonBusinessLayerModel> result = PersonBusinessLayerMapper.toBusinessModelList(null);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("toEntity: Mapping von BusinessModel zu Entity")
    void testToEntity() {
        // Act
        PersonDataLayerModel result = PersonBusinessLayerMapper.toEntity(model);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Person", result.getName());
        assertEquals("+123456789", result.getPhoneNumber());
    }

    @Test
    @DisplayName("toEntity: Null-BusinessModel gibt Null zurück")
    void testToEntityNull() {
        // Act
        PersonDataLayerModel result = PersonBusinessLayerMapper.toEntity(null);

        // Assert
        assertNull(result);
    }

    private PersonDataLayerModel createAnotherEntity() {
        PersonDataLayerModel another = new PersonDataLayerModel();
        another.setId(2L);
        another.setName("Another Person");
        another.setPhoneNumber("+987654321");
        return another;
    }
}
