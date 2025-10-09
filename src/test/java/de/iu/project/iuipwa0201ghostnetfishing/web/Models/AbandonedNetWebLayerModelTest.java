package de.iu.project.iuipwa0201ghostnetfishing.web.Models;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class AbandonedNetWebLayerModelTest {

    @Test
    void recordValues_areAccessible() {
        Instant now = Instant.now();
        AbandonedNetWebLayerModel m = new AbandonedNetWebLayerModel(9L, "Harbor", 7.5, "REPORTED", now, "Liz");

        assertEquals(9L, m.id());
        assertEquals("Harbor", m.location());
        assertEquals(7.5, m.size());
        assertEquals("REPORTED", m.status());
        assertEquals(now, m.createdAt());
        assertEquals("Liz", m.personName());
    }
}

