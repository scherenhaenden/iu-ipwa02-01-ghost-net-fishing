package de.iu.project.iuipwa0201ghostnetfishing.DatabaseLayer.Models;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class GhostNetDataLayerModelTest {

    @Test
    void constructorAndGettersSetters_workAsExpected() {
        PersonDataLayerModel person = new PersonDataLayerModel(5L, "Reporter", "+49");
        Date now = new Date();

        GhostNetDataLayerModel model = new GhostNetDataLayerModel(1L, "57.0,9.0", 12.5, NetStatusDataLayerEnum.REPORTED, now, person);

        assertEquals(1L, model.getId());
        assertEquals("57.0,9.0", model.getLocation());
        assertEquals(12.5, model.getSize());
        assertEquals(NetStatusDataLayerEnum.REPORTED, model.getStatus());
        assertEquals(now, model.getCreatedAt());
        assertNotNull(model.getPerson());
        assertEquals(5L, model.getPerson().getId());

        // setters
        model.setId(2L);
        model.setLocation("10.0,20.0");
        model.setSize(3.14);
        model.setStatus(NetStatusDataLayerEnum.RECOVERED);
        model.setPerson(new PersonDataLayerModel(7L, "Other", null));

        assertEquals(2L, model.getId());
        assertEquals("10.0,20.0", model.getLocation());
        assertEquals(3.14, model.getSize());
        assertEquals(NetStatusDataLayerEnum.RECOVERED, model.getStatus());
        assertEquals(7L, model.getPerson().getId());
    }

    @Test
    void constructedWithLocalDateTime_setsCreatedAtProperly() {
        LocalDateTime ldt = LocalDateTime.of(2020, 1, 2, 3, 4);
        GhostNetDataLayerModel model = new GhostNetDataLayerModel("L", 1.0, NetStatusDataLayerEnum.MISSING, ldt, null);

        assertNotNull(model.getCreatedAt());
        // createdAt should reflect the provided LocalDateTime (rough check: year)
        assertEquals(2020, new Date(model.getCreatedAt().getTime()).toInstant().atZone(java.time.ZoneId.systemDefault()).getYear());
    }

    @Test
    void onPrePersist_setsCreatedAt_whenNull() {
        GhostNetDataLayerModel model = new GhostNetDataLayerModel(null, 0.0, NetStatusDataLayerEnum.REPORTED, null);
        // createdAt is initialized in constructor variants except the explicit-null one, so force null then call hook
        model.setCreatedAt(null);
        model.onPrePersist();
        assertNotNull(model.getCreatedAt());
    }

    @Test
    void toString_includesImportantFields() {
        PersonDataLayerModel person = new PersonDataLayerModel(11L, "P", null);
        GhostNetDataLayerModel model = new GhostNetDataLayerModel(3L, "loc", 2.2, NetStatusDataLayerEnum.RECOVERY_PENDING, new Date(), person);

        String s = model.toString();
        assertTrue(s.contains("id=3"));
        assertTrue(s.contains("loc"));
        assertTrue(s.contains("2.2"));
        assertTrue(s.contains("RECOVERY_PENDING"));
        assertTrue(s.contains("11"));
    }
}
