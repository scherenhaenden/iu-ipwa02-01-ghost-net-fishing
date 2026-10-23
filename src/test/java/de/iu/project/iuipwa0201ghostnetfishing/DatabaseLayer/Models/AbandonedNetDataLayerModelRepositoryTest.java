package de.iu.project.iuipwa0201ghostnetfishing.DatabaseLayer.Models;

import de.iu.project.iuipwa0201ghostnetfishing.DatabaseLayer.Repositories.AbandonedNetDataLayerModelRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class AbandonedNetDataLayerModelRepositoryTest {

    @Autowired
    private AbandonedNetDataLayerModelRepository repository;

    @Test
    @Rollback
    void newestFirst_ordering_is_enforced_by_query() {
        // Arrange: three nets with different createdAt timestamps
        AbandonedNetDataLayerModel older = new AbandonedNetDataLayerModel();
        older.setLocation("older");
        older.setSize(1.0);
        older.setStatus(NetStatusDataLayerEnum.REPORTED);
        older.setCreatedAt(Date.from(Instant.now().minus(2, ChronoUnit.DAYS)));

        AbandonedNetDataLayerModel middle = new AbandonedNetDataLayerModel();
        middle.setLocation("middle");
        middle.setSize(2.0);
        middle.setStatus(NetStatusDataLayerEnum.REPORTED);
        middle.setCreatedAt(Date.from(Instant.now().minus(1, ChronoUnit.DAYS)));

        AbandonedNetDataLayerModel newest = new AbandonedNetDataLayerModel();
        newest.setLocation("newest");
        newest.setSize(3.0);
        newest.setStatus(NetStatusDataLayerEnum.REPORTED);
        newest.setCreatedAt(new Date());

        repository.save(older);
        repository.save(middle);
        repository.save(newest);

        // Act
        List<AbandonedNetDataLayerModel> found = repository.findAllByOrderByCreatedAtDesc();

        // Assert: size and ordering
        assertEquals(3, found.size(), "Expected three persisted nets");
        assertEquals("newest", found.get(0).getLocation(), "First element should be the newest");
        assertEquals("middle", found.get(1).getLocation(), "Second element should be the middle");
        assertEquals("older", found.get(2).getLocation(), "Third element should be the oldest");

        // Also ensure timestamps are in descending order
        assertTrue(found.get(0).getCreatedAt().compareTo(found.get(1).getCreatedAt()) >= 0);
        assertTrue(found.get(1).getCreatedAt().compareTo(found.get(2).getCreatedAt()) >= 0);
    }
}

