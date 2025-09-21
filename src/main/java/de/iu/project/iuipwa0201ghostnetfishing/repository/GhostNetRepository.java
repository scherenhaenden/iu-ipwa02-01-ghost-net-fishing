package de.iu.project.iuipwa0201ghostnetfishing.repository;

import de.iu.project.iuipwa0201ghostnetfishing.model.GhostNet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GhostNetRepository extends JpaRepository<GhostNet, Long> {
    /**
     * Retrieves a list of GhostNet objects ordered by creation date in descending order.
     */
    List<GhostNet> findAllByOrderByCreatedAtDesc();
}
