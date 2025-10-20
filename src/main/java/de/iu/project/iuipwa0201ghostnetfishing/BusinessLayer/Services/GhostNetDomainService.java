package de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Services;

import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Mappers.GhostNetBusinessLayerMapper;
import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Mappers.PersonBusinessLayerMapper;
import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Models.GhostNetBusinessLayerModel;
import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Models.NetStatusBusinessLayerEnum;
import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Models.PersonBusinessLayerModel;
import de.iu.project.iuipwa0201ghostnetfishing.DatabaseLayer.Models.GhostNetDataLayerModel;
import de.iu.project.iuipwa0201ghostnetfishing.DatabaseLayer.Models.NetStatusDataLayerEnum;
import de.iu.project.iuipwa0201ghostnetfishing.DatabaseLayer.Models.PersonDataLayerModel;
import de.iu.project.iuipwa0201ghostnetfishing.DatabaseLayer.Repositories.GhostNetDataLayerModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Domain service responsible for managing GhostNet entities in the business layer.
 * This service provides operations for creating, retrieving, updating, and deleting ghost nets,
 * as well as handling status transitions and person assignments, ensuring business rules are enforced.
 */
@Service
@Transactional
public class GhostNetDomainService {

    private final GhostNetDataLayerModelRepository repository;
    private final GhostNetBusinessLayerMapper mapper;
    private final PersonBusinessLayerMapper personMapper;

    @Autowired
    public GhostNetDomainService(GhostNetDataLayerModelRepository repository,
                                 GhostNetBusinessLayerMapper mapper,
                                 PersonBusinessLayerMapper personMapper) {
        this.repository = repository;
        this.mapper = mapper;
        this.personMapper = personMapper;
    }

    /**
     * Saves the given GhostNet business model to the database, applying default values if necessary.
     * If the status is null, it is set to REPORTED. If createdAt is null, it is set to the current instant.
     *
     * @param model the GhostNet business model to save
     * @return the saved GhostNet business model with applied defaults, or null if the input model was null
     */
    public GhostNetBusinessLayerModel save(GhostNetBusinessLayerModel model) {
        if (model == null) return null;
        // apply defaults
        if (model.getStatus() == null) {
            model.setStatus(NetStatusBusinessLayerEnum.REPORTED);
        }
        if (model.getCreatedAt() == null) {
            model.setCreatedAt(Instant.now());
        }
        GhostNetDataLayerModel entity = mapper.toEntity(model);
        GhostNetDataLayerModel saved = repository.save(entity);
        return mapper.toBusinessModel(saved);
    }

    /**
     * Finds a GhostNet by its ID.
     *
     * @param id the ID of the GhostNet to find
     * @return an Optional containing the GhostNet business model if found, or empty if not found or id is null
     */
    @Transactional(readOnly = true)
    public Optional<GhostNetBusinessLayerModel> findById(Long id) {
        if (id == null) return Optional.empty();
        Optional<GhostNetDataLayerModel> e = repository.findById(id);
        return e.map(mapper::toBusinessModel);
    }

    /**
     * Finds all GhostNets, optionally filtered by status, ordered by creation date descending.
     *
     * @param status an Optional containing the status to filter by, or empty to retrieve all
     * @return a list of GhostNet business models
     */
    @Transactional(readOnly = true)
    public List<GhostNetBusinessLayerModel> findAll(Optional<NetStatusBusinessLayerEnum> status) {
        Optional<NetStatusBusinessLayerEnum> safeStatus = (status == null) ? Optional.empty() : status;
        List<GhostNetDataLayerModel> entities;
        if (safeStatus.isEmpty()) {
            entities = repository.findAllByOrderByCreatedAtDesc();
        } else {
            NetStatusDataLayerEnum dataStatus = NetStatusDataLayerEnum.valueOf(safeStatus.get().name());
            entities = repository.findByStatusOrderByCreatedAtDesc(dataStatus);
        }
        return mapper.toBusinessModelList(entities);
    }

    /**
     * Assigns a person to a GhostNet for recovery, changing its status accordingly.
     * If the net is REPORTED, assigns the person and sets status to RECOVERY_PENDING.
     * If already RECOVERY_PENDING, checks for idempotency based on person name.
     * Otherwise, returns CONFLICT.
     *
     * @param id the ID of the GhostNet
     * @param personModel the person to assign
     * @return OperationResult indicating the outcome: OK, NOT_FOUND, BAD_REQUEST, or CONFLICT
     */
    public OperationResult assignPerson(Long id, PersonBusinessLayerModel personModel) {
        if (id == null) {
            return OperationResult.NOT_FOUND;
        }
        if (personModel == null) {
            return OperationResult.BAD_REQUEST;
        }
        Optional<GhostNetDataLayerModel> oe = repository.findById(id);
        if (oe.isEmpty()) {
            return OperationResult.NOT_FOUND;
        }
        GhostNetDataLayerModel entity = oe.get();
        if (entity.getStatus() == NetStatusDataLayerEnum.REPORTED) {
            // set person and change state using mapper
            PersonDataLayerModel personEntity = personMapper.toEntity(personModel);
            entity.setPerson(personEntity);
            entity.setStatus(NetStatusDataLayerEnum.RECOVERY_PENDING);
            repository.save(entity);
            return OperationResult.OK;
        } else if (entity.getStatus() == NetStatusDataLayerEnum.RECOVERY_PENDING) {
            // idempotency: if same person name => OK, otherwise conflict
            PersonDataLayerModel existing = entity.getPerson();
            String existingName = (existing != null) ? existing.getName() : null;
            String requestedName = (personModel.getName() != null) ? personModel.getName() : null;
            if (existingName != null && existingName.equals(requestedName)) {
                return OperationResult.OK;
            }
            return OperationResult.CONFLICT;
        }
        return OperationResult.CONFLICT;
    }

    /**
     * Marks a GhostNet as recovered if it is in RECOVERY_PENDING status.
     *
     * @param id the ID of the GhostNet
     * @return OperationResult: OK if marked, NOT_FOUND if not found, CONFLICT if not in correct status
     */
    public OperationResult markRecovered(Long id) {
        if (id == null) return OperationResult.NOT_FOUND;
        Optional<GhostNetDataLayerModel> oe = repository.findById(id);
        if (oe.isEmpty()) return OperationResult.NOT_FOUND;
        GhostNetDataLayerModel entity = oe.get();
        if (entity.getStatus() == NetStatusDataLayerEnum.RECOVERY_PENDING) {
            entity.setStatus(NetStatusDataLayerEnum.RECOVERED);
            repository.save(entity);
            return OperationResult.OK;
        }
        return OperationResult.CONFLICT;
    }

    /**
     * Marks a GhostNet as missing if it is in REPORTED or RECOVERY_PENDING status.
     * Idempotent if already MISSING.
     *
     * @param id the ID of the GhostNet
     * @return OperationResult: OK if marked or already missing, NOT_FOUND if not found,
     * CONFLICT if in RECOVERED status
     */
    public OperationResult markAsMissing(Long id) {
        if (id == null) return OperationResult.NOT_FOUND;
        Optional<GhostNetDataLayerModel> oe = repository.findById(id);
        if (oe.isEmpty()) return OperationResult.NOT_FOUND;
        GhostNetDataLayerModel entity = oe.get();

        // Allow marking as missing from REPORTED or RECOVERY_PENDING status
        if (entity.getStatus() == NetStatusDataLayerEnum.REPORTED ||
                entity.getStatus() == NetStatusDataLayerEnum.RECOVERY_PENDING) {
            entity.setStatus(NetStatusDataLayerEnum.MISSING);
            repository.save(entity);
            return OperationResult.OK;
        } else if (entity.getStatus() == NetStatusDataLayerEnum.MISSING) {
            // Idempotent: already missing -> OK
            return OperationResult.OK;
        }    // Cannot mark as missing from RECOVERED status
        return OperationResult.CONFLICT;
    }

    /**
     * Deletes a GhostNet by its ID.
     *
     * @param id the ID of the GhostNet to delete
     * @return OperationResult: OK if deleted, NOT_FOUND if not found
     */
    public OperationResult deleteById(Long id) {
        if (id == null) return OperationResult.NOT_FOUND;
        Optional<GhostNetDataLayerModel> oe = repository.findById(id);
        if (oe.isEmpty()) return OperationResult.NOT_FOUND;
        repository.deleteById(id);
        return OperationResult.OK;
    }
}
