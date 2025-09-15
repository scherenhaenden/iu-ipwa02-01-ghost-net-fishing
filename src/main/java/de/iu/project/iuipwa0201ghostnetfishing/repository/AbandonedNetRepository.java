package de.iu.project.iuipwa0201ghostnetfishing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import de.iu.project.iuipwa0201ghostnetfishing.model.AbandonedNet;
import de.iu.project.iuipwa0201ghostnetfishing.model.NetStatus;

/* AbandonedNetRepository interface
   Spring Data JPA repository for AbandonedNet entity CRUD and custom queries.
   Extends JpaRepository for built-in methods like save, findById, delete.
   Derived queries are automatically implemented based on method names.
*/
@Repository
public interface AbandonedNetRepository extends JpaRepository<AbandonedNet, Long> {

    /* Derived query: all nets newest first
       Fetches all AbandonedNet records sorted by createdAt descending (most recent first).
       SQL generated: SELECT * FROM ABANDONED_NET ORDER BY CREATED_AT DESC
    */
    List<AbandonedNet> findAllByOrderByCreatedAtDesc();

    /* Derived query by status
       Retrieves AbandonedNet entities filtered by a specific NetStatus value.
       SQL generated: SELECT * FROM ABANDONED_NET WHERE STATUS = ?
    */
    List<AbandonedNet> findByStatus(NetStatus status);
}
