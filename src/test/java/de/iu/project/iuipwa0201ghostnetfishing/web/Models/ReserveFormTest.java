package de.iu.project.iuipwa0201ghostnetfishing.web.Models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReserveFormTest {

    @Test
    void gettersSettersAndTrimHelper_workAsExpected() {
        ReserveForm r = new ReserveForm();
        assertNull(r.getPersonName());
        assertNull(r.getTrimmedPersonName());

        r.setPersonName("  Bob  ");
        assertEquals("  Bob  ", r.getPersonName());
        assertEquals("Bob", r.getTrimmedPersonName());

        r.setPersonName(null);
        assertNull(r.getTrimmedPersonName());
    }
}

