package de.iu.project.iuipwa0201ghostnetfishing.web.Models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MissingFormTest {

    @Test
    void gettersAndSetters_workAsExpected() {
        MissingForm m = new MissingForm();
        assertNull(m.getNotes());

        m.setNotes("Was here");
        assertEquals("Was here", m.getNotes());

        m.setNotes(null);
        assertNull(m.getNotes());
    }
}

