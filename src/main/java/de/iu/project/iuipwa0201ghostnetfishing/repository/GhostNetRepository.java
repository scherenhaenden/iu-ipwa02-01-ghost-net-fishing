package de.iu.project.iuipwa0201ghostnetfishing.repository;

import de.iu.project.iuipwa0201ghostnetfishing.model.GhostNet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/* GhostNetRepository interface
   Spring Data JPA repository for GhostNet entity operations.
   Extends JpaRepository for automatic CRUD methods; adds custom derived queries.
   All methods are non-blocking and transaction-managed by Spring.
*/
public interface GhostNetRepository extends JpaRepository<GhostNet, Long> {

    /* Derived query method
       Retrieves all GhostNet entities ordered by creation date descending (newest first).
       Automatically generates SQL: SELECT * FROM GHOST_NET ORDER BY CREATED_AT DESC
    */
    List<GhostNet> findAllByOrderByCreatedAtDesc();
}
