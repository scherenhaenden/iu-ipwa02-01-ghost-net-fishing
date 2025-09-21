package de.iu.project.iuipwa0201ghostnetfishing.DatabaseLayer.Repositories;

import de.iu.project.iuipwa0201ghostnetfishing.DatabaseLayer.Models.AbandonedNetDataLayerModel;
import de.iu.project.iuipwa0201ghostnetfishing.DatabaseLayer.Models.NetStatusDataLayerEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;


/* AbandonedNetRepository interface
   Spring Data JPA repository for AbandonedNet entity CRUD and custom queries.
   Extends JpaRepository for built-in methods like save, findById, delete.
   Derived queries are automatically implemented based on method names.
*/
@Repository
public interface AbandonedNetDataLayerModelRepository extends JpaRepository<AbandonedNetDataLayerModel, Long> {

    /* Derived query: all nets newest first
       Fetches all AbandonedNet records sorted by createdAt descending (most recent first).
       SQL generated: SELECT * FROM ABANDONED_NET ORDER BY CREATED_AT DESC
    */
    List<AbandonedNetDataLayerModel> findAllByOrderByCreatedAtDesc();

    /* Derived query by status
       Retrieves AbandonedNet entities filtered by a specific NetStatus value.
       SQL generated: SELECT * FROM ABANDONED_NET WHERE STATUS = ?
    */
    List<AbandonedNetDataLayerModel> findByStatus(NetStatusDataLayerEnum status);
}
