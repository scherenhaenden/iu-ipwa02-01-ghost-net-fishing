package de.iu.ipwa02.ghostnet.service;

import de.iu.ipwa02.ghostnet.model.GhostNet;
import de.iu.ipwa02.ghostnet.model.NetStatus;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * Service for managing Ghost Net operations
 */
@Stateless
public class GhostNetService {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Create a new ghost net report
     */
    public GhostNet createGhostNet(GhostNet ghostNet) {
        entityManager.persist(ghostNet);
        return ghostNet;
    }

    /**
     * Find all ghost nets
     */
    public List<GhostNet> findAllGhostNets() {
        TypedQuery<GhostNet> query = entityManager.createNamedQuery("GhostNet.findAll", GhostNet.class);
        return query.getResultList();
    }

    /**
     * Find ghost nets by status
     */
    public List<GhostNet> findGhostNetsByStatus(NetStatus status) {
        TypedQuery<GhostNet> query = entityManager.createNamedQuery("GhostNet.findByStatus", GhostNet.class);
        query.setParameter("status", status);
        return query.getResultList();
    }

    /**
     * Find ghost net by ID
     */
    public GhostNet findGhostNetById(Long id) {
        return entityManager.find(GhostNet.class, id);
    }

    /**
     * Update ghost net
     */
    public GhostNet updateGhostNet(GhostNet ghostNet) {
        return entityManager.merge(ghostNet);
    }

    /**
     * Delete ghost net
     */
    public void deleteGhostNet(Long id) {
        GhostNet ghostNet = findGhostNetById(id);
        if (ghostNet != null) {
            entityManager.remove(ghostNet);
        }
    }

    /**
     * Mark ghost net as recovered
     */
    public void markAsRecovered(Long id) {
        GhostNet ghostNet = findGhostNetById(id);
        if (ghostNet != null) {
            ghostNet.setStatus(NetStatus.RECOVERED);
            updateGhostNet(ghostNet);
        }
    }
}