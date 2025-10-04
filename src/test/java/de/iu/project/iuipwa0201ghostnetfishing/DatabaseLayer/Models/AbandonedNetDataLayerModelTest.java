package de.iu.project.iuipwa0201ghostnetfishing.DatabaseLayer.Models;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class AbandonedNetDataLayerModelTest {

    @Test
    void protectedNoArgConstructor_initializesCreatedAt() {
        AbandonedNetDataLayerModel m = new AbandonedNetDataLayerModel();
        assertNotNull(m.getCreatedAt(), "createdAt should be initialized by default");
    }

    @Test
    void allArgsConstructor_setsFields_andCreatedAtIsGenerated() {
        var person = new PersonDataLayerModel(3L, "Pete", "+49");
        AbandonedNetDataLayerModel m = new AbandonedNetDataLayerModel(5L, "Beach", 2.0, NetStatusDataLayerEnum.REPORTED, person);

        assertEquals(5L, m.getId());
        assertEquals("Beach", m.getLocation());
        assertEquals(2.0, m.getSize());
        assertEquals(NetStatusDataLayerEnum.REPORTED, m.getStatus());
        assertEquals(person, m.getPerson());
        assertNotNull(m.getCreatedAt());
    }

    @Test
    void constructor_withExplicitDate_allowsNullCreatedAt() {
        var person = new PersonDataLayerModel(7L, "Ann", null);
        AbandonedNetDataLayerModel m = new AbandonedNetDataLayerModel(9L, "Harbor", 4.5, NetStatusDataLayerEnum.MISSING, null, person);

        assertEquals(9L, m.getId());
        assertNull(m.getCreatedAt());
        assertEquals(NetStatusDataLayerEnum.MISSING, m.getStatus());
    }

    @Test
    void localDateTimeConstructor_convertsToDate() {
        var person = new PersonDataLayerModel(2L, "Zoe", "");
        LocalDateTime now = LocalDateTime.now().withNano(0);
        AbandonedNetDataLayerModel m = new AbandonedNetDataLayerModel("Cove", 1.1, NetStatusDataLayerEnum.REPORTED, now, person);

        assertEquals("Cove", m.getLocation());
        assertEquals(1.1, m.getSize());
        assertEquals(person, m.getPerson());
        assertNotNull(m.getCreatedAt());
        // Rough check: createdAt time >= now.minusSeconds(5)
        Date created = m.getCreatedAt();
        assertTrue(created.toInstant().isAfter(now.minusSeconds(5).atZone(java.time.ZoneId.systemDefault()).toInstant())
                || created.toInstant().equals(now.atZone(java.time.ZoneId.systemDefault()).toInstant()));
    }

    @Test
    void settersAndToString_reflectChanges() {
        AbandonedNetDataLayerModel m = new AbandonedNetDataLayerModel();
        m.setId(11L);
        m.setLocation("Pier");
        m.setSize(8.8);
        m.setStatus(NetStatusDataLayerEnum.RECOVERED);
        var p = new PersonDataLayerModel(12L, "Mark", null);
        m.setPerson(p);
        Date d = new Date();
        m.setCreatedAt(d);

        assertEquals(11L, m.getId());
        assertEquals("Pier", m.getLocation());
        assertEquals(8.8, m.getSize());
        assertEquals(NetStatusDataLayerEnum.RECOVERED, m.getStatus());
        assertEquals(p, m.getPerson());
        assertEquals(d, m.getCreatedAt());

        String s = m.toString();
        assertTrue(s.contains("11"));
        assertTrue(s.contains("Pier"));
        assertTrue(s.contains("Mark"));
    }
}
