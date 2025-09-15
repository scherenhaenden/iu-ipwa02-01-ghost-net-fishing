package de.iu.project.iuipwa0201ghostnetfishing.service;

import de.iu.project.iuipwa0201ghostnetfishing.model.GhostNet;
import de.iu.project.iuipwa0201ghostnetfishing.repository.GhostNetRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/* GhostNetService class
   Service layer for business logic related to GhostNet entities.
   Provides methods for creation and retrieval; integrates with GhostNetRepository.
   Designed for use in web controllers or other components.
*/
@Service
public class GhostNetService {

    /* Repository dependency
       Injected via constructor for access to GhostNet data operations.
    */
    private final GhostNetRepository repository;

    /* Constructor
       Initializes the service with the GhostNetRepository instance.
    */
    public GhostNetService(GhostNetRepository repository) {
        this.repository = repository;
    }

    /* Create and persist a new GhostNet
       Builds a new GhostNet instance with the provided name and saves it to the database.
       Automatically sets the createdAt timestamp.
    */
    public GhostNet create(String name) {
        GhostNet g = new GhostNet(name);
        return repository.save(g);
    }

    /* Retrieve all GhostNets ordered by creation date descending
       Fetches all GhostNet records sorted newest first using the repository's derived query.
       Useful for displaying recent reports.
    */
    public List<GhostNet> findAll() {
        return repository.findAllByOrderByCreatedAtDesc();
    }
}
