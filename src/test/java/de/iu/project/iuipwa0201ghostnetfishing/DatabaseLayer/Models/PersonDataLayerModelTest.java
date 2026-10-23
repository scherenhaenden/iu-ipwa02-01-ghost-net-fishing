package de.iu.project.iuipwa0201ghostnetfishing.DatabaseLayer.Models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PersonDataLayerModelTest {

    @Test
    void constructorAndGettersSetters_workAsExpected() {
        PersonDataLayerModel p = new PersonDataLayerModel(10L, "Alice", "+491234");

        assertEquals(10L, p.getId());
        assertEquals("Alice", p.getName());
        assertEquals("+491234", p.getPhoneNumber());

        // setters
        p.setId(11L);
        p.setName("Bob");
        p.setPhoneNumber(null);

        assertEquals(11L, p.getId());
        assertEquals("Bob", p.getName());
        assertNull(p.getPhoneNumber());
    }

    @Test
    void toString_containsIdAndName() {
        PersonDataLayerModel p = new PersonDataLayerModel(7L, "Charlie", "");
        String s = p.toString();
        assertTrue(s.contains("7"));
        assertTrue(s.contains("Charlie"));
    }
}

