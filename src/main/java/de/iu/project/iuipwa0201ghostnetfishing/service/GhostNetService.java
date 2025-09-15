package de.iu.project.iuipwa0201ghostnetfishing.service;

import de.iu.project.iuipwa0201ghostnetfishing.model.GhostNet;
import de.iu.project.iuipwa0201ghostnetfishing.repository.GhostNetRepository;

import java.util.List;

public class GhostNetService {

    private final GhostNetRepository repository;

    public GhostNetService(GhostNetRepository repository) {
        this.repository = repository;
    }

    /**
     * Creates and persists a new GhostNet instance with the given name.
     */
    public GhostNet create(String name) {
        GhostNet g = new GhostNet(name);
        return repository.save(g);
    }

    /**
     * Retrieves a list of all GhostNet entities ordered by creation date in descending order.
     */
    public List<GhostNet> findAll() {
        return repository.findAllByOrderByCreatedAtDesc();
    }
}
