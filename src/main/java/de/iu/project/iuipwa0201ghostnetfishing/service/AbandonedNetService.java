package de.iu.project.iuipwa0201ghostnetfishing.service;

import de.iu.project.iuipwa0201ghostnetfishing.model.AbandonedNet;
import de.iu.project.iuipwa0201ghostnetfishing.model.NetStatus;
import de.iu.project.iuipwa0201ghostnetfishing.repository.AbandonedNetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/* AbandonedNetService class
   Business logic service layer for AbandonedNet entities.
   Handles CRUD operations via the injected repository; uses @Transactional for data consistency.
   Methods are designed for use in controllers or other services.
*/
@Service
public class AbandonedNetService {

    /* Repository dependency
       Injected via constructor for database access. Ensures loose coupling.
    */
    private final AbandonedNetRepository repository;

    /* Constructor
       Initializes the service with the required repository instance.
    */
    public AbandonedNetService(AbandonedNetRepository repository) {
        this.repository = repository;
    }

    /* Retrieve all nets sorted newest first
       Calls the repository's derived query to get all records in descending createdAt order.
       No transaction needed for read-only operation.
    */
    public List<AbandonedNet> getAllNetsNewestFirst() {
        return repository.findAllByOrderByCreatedAtDesc();
    }

    /* Save or update a net
       Persists the AbandonedNet entity to the database.
       @Transactional ensures atomicity if the save involves multiple steps.
    */
    @Transactional
    public AbandonedNet save(AbandonedNet net) {
        return repository.save(net);
    }

    /* Find nets by status
       Retrieves a list of AbandonedNet entities matching the given NetStatus.
       Read-only transaction for optimized query performance.
    */
    @Transactional(readOnly = true)
    public List<AbandonedNet> findByStatus(NetStatus status) {
        return repository.findByStatus(status);
    }

    /* Delete net by ID
       Removes the AbandonedNet entity with the specified ID from the database.
       @Transactional handles the delete operation atomically.
    */
    @Transactional
    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
