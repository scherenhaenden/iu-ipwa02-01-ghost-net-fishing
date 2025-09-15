package de.iu.project.iuipwa0201ghostnetfishing.service;

import de.iu.project.iuipwa0201ghostnetfishing.model.AbandonedNet;
import de.iu.project.iuipwa0201ghostnetfishing.model.GhostNet;
import de.iu.project.iuipwa0201ghostnetfishing.model.NetStatus;
import de.iu.project.iuipwa0201ghostnetfishing.model.Person;
import de.iu.project.iuipwa0201ghostnetfishing.repository.AbandonedNetRepository;
import de.iu.project.iuipwa0201ghostnetfishing.repository.GhostNetRepository;
import de.iu.project.iuipwa0201ghostnetfishing.repository.PersonRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/* GhostNetService class
   Service layer for business logic related to GhostNet and AbandonedNet entities.
   Provides methods for creation and retrieval of GhostNet records (legacy) and
   higher-level domain operations for AbandonedNet such as createNewNet, assignPersonToNet,
   and updateNetStatus. Transactional annotations ensure data consistency.
*/
@Service
public class GhostNetService {

    /* Repository dependencies
       GhostNetRepository: legacy entity operations used by existing controllers/views.
       AbandonedNetRepository + PersonRepository: domain repositories for new service APIs.
    */
    private final GhostNetRepository ghostRepository;
    private final AbandonedNetRepository abandonedRepository;
    private final PersonRepository personRepository;

    /* Constructor
       Constructor injection of repositories ensures immutability and easier testing.
    */
    public GhostNetService(GhostNetRepository ghostRepository,
                           AbandonedNetRepository abandonedRepository,
                           PersonRepository personRepository) {
        this.ghostRepository = ghostRepository;
        this.abandonedRepository = abandonedRepository;
        this.personRepository = personRepository;
    }

    /* Legacy GhostNet methods */

    /* Create and persist a new GhostNet (legacy entity used by web UI). */
    public GhostNet create(String name) {
        GhostNet g = new GhostNet(name);
        return ghostRepository.save(g);
    }

    /* Retrieve all GhostNet entries newest first. */
    public List<GhostNet> findAll() {
        return ghostRepository.findAllByOrderByCreatedAtDesc();
    }

    /* New domain methods for AbandonedNet */

    /**
     * Create and persist a new AbandonedNet with default status REPORTED.
     */
    public AbandonedNet createNewNet(String location, Double size) {
        AbandonedNet net = new AbandonedNet(
                location,
                size,
                NetStatus.REPORTED,
                LocalDateTime.now(),
                null
        );
        return abandonedRepository.save(net);
    }

    /**
     * Link an existing person to a net they reported or recovered.
     * Transactional to ensure the association is persisted atomically.
     */
    @Transactional
    public AbandonedNet assignPersonToNet(Long netId, Long personId) {
        AbandonedNet net = abandonedRepository.findById(netId)
                .orElseThrow(() -> new IllegalArgumentException("Net not found"));
        Person person = personRepository.findById(personId)
                .orElseThrow(() -> new IllegalArgumentException("Person not found"));

        net.setPerson(person);
        return abandonedRepository.save(net);
    }

    /**
     * Update the status of an AbandonedNet (e.g., RECOVERED).
     */
    @Transactional
    public AbandonedNet updateNetStatus(Long netId, NetStatus newStatus) {
        AbandonedNet net = abandonedRepository.findById(netId)
                .orElseThrow(() -> new IllegalArgumentException("Net not found"));
        net.setStatus(newStatus);
        return abandonedRepository.save(net);
    }

    /** Convenience read method */
    public List<AbandonedNet> getAllAbandonedNetsNewestFirst() {
        return abandonedRepository.findAllByOrderByCreatedAtDesc();
    }
}
