package de.iu.project.iuipwa0201ghostnetfishing.web.Models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PersonWebLayerModelTest {

    @Test
    void recordValues_areAccessible() {
        PersonWebLayerModel p = new PersonWebLayerModel(5L, "Anna", "+4900");

        assertEquals(5L, p.id());
        assertEquals("Anna", p.name());
        assertEquals("+4900", p.phoneNumber());
    }
}

