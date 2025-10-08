package de.iu.project.iuipwa0201ghostnetfishing.web.Models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GhostNetFormTest {

    @Test
    void gettersAndSetters_workAsExpected() {
        GhostNetForm f = new GhostNetForm();
        assertNull(f.getLocation());
        assertNull(f.getSize());
        assertNull(f.getPersonName());
        assertNull(f.getNotes());

        f.setLocation("Beach");
        f.setSize(3.14);
        f.setPersonName("Alice");
        f.setNotes("Found near pier");

        assertEquals("Beach", f.getLocation());
        assertEquals(3.14, f.getSize());
        assertEquals("Alice", f.getPersonName());
        assertEquals("Found near pier", f.getNotes());

        // allow nulls
        f.setSize(null);
        assertNull(f.getSize());
    }
}

