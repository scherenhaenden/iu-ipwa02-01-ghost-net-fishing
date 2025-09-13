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

    public GhostNet create(String name) {
        GhostNet g = new GhostNet(name);
        em.persist(g);
        return g;
    }

    public List<GhostNet> findAll() {
        return em.createQuery("SELECT g FROM GhostNet g ORDER BY g.createdAt DESC", GhostNet.class)
                .getResultList();
    }
}

