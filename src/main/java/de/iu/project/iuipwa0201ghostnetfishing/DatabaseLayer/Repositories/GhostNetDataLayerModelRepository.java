package de.iu.project.iuipwa0201ghostnetfishing.DatabaseLayer.Repositories;

import de.iu.project.iuipwa0201ghostnetfishing.DatabaseLayer.Models.GhostNetDataLayerModel;
import de.iu.project.iuipwa0201ghostnetfishing.DatabaseLayer.Models.NetStatusDataLayerEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/* GhostNetRepository interface
   Spring Data JPA repository for GhostNet entity operations.
   Extends JpaRepository for automatic CRUD methods; adds custom derived queries.
   All methods are non-blocking and transaction-managed by Spring.
*/
public interface GhostNetDataLayerModelRepository extends JpaRepository<GhostNetDataLayerModel, Long> {

    /* Derived query method
       Retrieves all GhostNet entities ordered by creation date descending (newest first).
       Automatically generates SQL: SELECT * FROM GHOST_NET ORDER BY CREATED_AT DESC
    */
    List<GhostNetDataLayerModel> findAllByOrderByCreatedAtDesc();

    /* Derived query by status
       Retrieves GhostNet entities filtered by a specific NetStatus value.
       SQL generated: SELECT * FROM GHOST_NET WHERE STATUS = ?
    */
    List<GhostNetDataLayerModel> findByStatus(NetStatusDataLayerEnum status);

    /* Derived query by status ordered by createdAt desc
       Convenience method returning results filtered by status and sorted newest-first.
    */
    List<GhostNetDataLayerModel> findByStatusOrderByCreatedAtDesc(NetStatusDataLayerEnum status);
}
