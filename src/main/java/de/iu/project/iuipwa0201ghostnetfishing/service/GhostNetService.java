package de.iu.project.iuipwa0201ghostnetfishing.service;

import de.iu.project.iuipwa0201ghostnetfishing.model.GhostNet;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class GhostNetService {

    @PersistenceContext(unitName = "GhostNetPU")
    private EntityManager em;

    /**
     * Creates and persists a new GhostNet instance with the given name.
     */
    public GhostNet create(String name) {
        GhostNet g = new GhostNet(name);
        em.persist(g);
        return g;
    }

    /**
     * Retrieves a list of all GhostNet entities ordered by creation date in descending order.
     */
    public List<GhostNet> findAll() {
        return em.createQuery("SELECT g FROM GhostNet g ORDER BY g.createdAt DESC", GhostNet.class)
                .getResultList();
    }
}

