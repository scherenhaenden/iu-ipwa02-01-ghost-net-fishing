package de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Services;

import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Mappers.GhostNetBusinessLayerMapper;
import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Models.GhostNetBusinessLayerModel;
import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Models.NetStatusBusinessLayerEnum;
import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Models.PersonBusinessLayerModel;
import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Mappers.PersonBusinessLayerMapper;
import de.iu.project.iuipwa0201ghostnetfishing.DatabaseLayer.Models.GhostNetDataLayerModel;
import de.iu.project.iuipwa0201ghostnetfishing.DatabaseLayer.Models.NetStatusDataLayerEnum;
import de.iu.project.iuipwa0201ghostnetfishing.DatabaseLayer.Repositories.GhostNetDataLayerModelRepository;
import de.iu.project.iuipwa0201ghostnetfishing.exceptions.ResourceNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.time.Instant; // added for createdAt handling

@Service
public class GhostNetBusinessLayerService implements IGhostNetBusinessLayerService {

    private final GhostNetDataLayerModelRepository repository;
    private final GhostNetBusinessLayerMapper mapper;
    private final PersonBusinessLayerMapper personMapper;

    // Dependency injection via constructor (best practice)
    public GhostNetBusinessLayerService(GhostNetDataLayerModelRepository repository, GhostNetBusinessLayerMapper mapper, PersonBusinessLayerMapper personMapper) {
        this.repository = repository;
        this.mapper = mapper;
        this.personMapper = personMapper;
    }

    @Override
    @Transactional(readOnly = true) // Read-only transaction is more efficient
    public List<GhostNetBusinessLayerModel> findAll() {
        List<GhostNetDataLayerModel> entities = repository.findAllByOrderByCreatedAtDesc();
        return mapper.toBusinessModelList(entities);
    }

    @Override
    @Transactional
    public GhostNetBusinessLayerModel save(GhostNetBusinessLayerModel netBusinessModel) {
        // Guard: ensure US1 invariants before mapping/persisting
        if (netBusinessModel == null) {
            return null;
        }
        if (netBusinessModel.getStatus() == null) {
            netBusinessModel.setStatus(NetStatusBusinessLayerEnum.REPORTED);
        }
        if (netBusinessModel.getCreatedAt() == null) {
            netBusinessModel.setCreatedAt(Instant.now());
        }

        GhostNetDataLayerModel entityToSave = mapper.toEntity(netBusinessModel);
        GhostNetDataLayerModel savedEntity = repository.save(entityToSave);
        return mapper.toBusinessModel(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GhostNetBusinessLayerModel> findByStatus(NetStatusBusinessLayerEnum status) {
        // Convert business enum to data-layer enum
        NetStatusDataLayerEnum dataLayerStatus = NetStatusDataLayerEnum.valueOf(status.name());
        // Use ordered repository method to return newest first
        List<GhostNetDataLayerModel> entities = repository.findByStatusOrderByCreatedAtDesc(dataLayerStatus);
        return mapper.toBusinessModelList(entities);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public GhostNetBusinessLayerModel findByIdOrThrow(Long id) {
        GhostNetDataLayerModel entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("GhostNet with id " + id + " not found"));
        return mapper.toBusinessModel(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<GhostNetBusinessLayerModel> findById(Long id) {
        return repository.findById(id).map(mapper::toBusinessModel);
    }

    // New for US2: reserve a net non-exceptionally
    @Override
    @Transactional
    public OperationResult reserve(Long id, PersonBusinessLayerModel person) {
        if (id == null) return OperationResult.NOT_FOUND;
        if (person == null) return OperationResult.BAD_REQUEST;
        Optional<GhostNetDataLayerModel> oe = repository.findById(id);
        if (oe.isEmpty()) return OperationResult.NOT_FOUND;
        GhostNetDataLayerModel entity = oe.get();
        if (entity.getStatus() == NetStatusDataLayerEnum.REPORTED) {
            // set person and change state
            entity.setPerson(personMapper.toEntity(person));
            entity.setStatus(NetStatusDataLayerEnum.RECOVERY_PENDING);
            repository.save(entity);
            return OperationResult.OK;
        } else if (entity.getStatus() == NetStatusDataLayerEnum.RECOVERY_PENDING) {
            // idempotent: same person -> OK, different person -> CONFLICT
            var existing = entity.getPerson();
            String existingName = existing != null ? existing.getName() : null;
            String requestedName = person.getName();
            if (existingName != null && existingName.equals(requestedName)) {
                return OperationResult.OK;
            } else {
                return OperationResult.CONFLICT;
            }
        } else {
            return OperationResult.CONFLICT;
        }
    }
}
