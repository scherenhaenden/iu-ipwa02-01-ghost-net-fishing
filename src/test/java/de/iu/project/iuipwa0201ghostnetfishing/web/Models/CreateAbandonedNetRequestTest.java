package de.iu.project.iuipwa0201ghostnetfishing.web.Models;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;

import static org.junit.jupiter.api.Assertions.*;

class CreateAbandonedNetRequestTest {

    @Test
    void noArgConstructor_andSettersGetters_workAsExpected() {
        CreateAbandonedNetRequest r = new CreateAbandonedNetRequest();
        assertNull(r.getLocation());
        assertNull(r.getSize());
        assertNull(r.getPersonName());

        r.setLocation("Bay");
        r.setSize(4.2);
        r.setPersonName("Pete");

        assertEquals("Bay", r.getLocation());
        assertEquals(4.2, r.getSize());
        assertEquals("Pete", r.getPersonName());

        r.setPersonName(null);
        assertNull(r.getPersonName());
    }

    @Test
    void allArgsConstructor_setsValues() throws Exception {
        Constructor<CreateAbandonedNetRequest> ctor = CreateAbandonedNetRequest.class.getConstructor(String.class, Double.class, String.class);
        CreateAbandonedNetRequest r = ctor.newInstance("Cove", 1.0, "Sam");

        assertEquals("Cove", r.getLocation());
        assertEquals(1.0, r.getSize());
        assertEquals("Sam", r.getPersonName());
    }
}

