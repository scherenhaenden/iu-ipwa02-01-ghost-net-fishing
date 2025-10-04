package de.iu.project.iuipwa0201ghostnetfishing.web.Models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RecoverFormTest {

    @Test
    void gettersAndSetters_workAsExpected() {
        RecoverForm r = new RecoverForm();
        assertNull(r.getNotes());

        r.setNotes("Recovered near buoy");
        assertEquals("Recovered near buoy", r.getNotes());

        r.setNotes(null);
        assertNull(r.getNotes());
    }
}

