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

@Service
@Transactional
public class GhostNetDomainService {

    private final GhostNetDataLayerModelRepository repository;
    private final GhostNetBusinessLayerMapper mapper;
    private final PersonBusinessLayerMapper personMapper;

    @Autowired
    public GhostNetDomainService(GhostNetDataLayerModelRepository repository, GhostNetBusinessLayerMapper mapper, PersonBusinessLayerMapper personMapper) {
        this.repository = repository;
        this.mapper = mapper;
        this.personMapper = personMapper;
    }

    // save: apply defaults (status -> REPORTED if null, createdAt -> now if null)
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

    // findById -> Optional
    @Transactional(readOnly = true)
    public Optional<GhostNetBusinessLayerModel> findById(Long id) {
        if (id == null) return Optional.empty();
        Optional<GhostNetDataLayerModel> e = repository.findById(id);
        return e.map(mapper::toBusinessModel);
    }

    // findAll(Optional<status>)
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

    // assignPerson(id, person) -> OperationResult
    public OperationResult assignPerson(Long id, PersonBusinessLayerModel personModel) {
        if (id == null) return OperationResult.NOT_FOUND;
        if (personModel == null) return OperationResult.BAD_REQUEST;
        Optional<GhostNetDataLayerModel> oe = repository.findById(id);
        if (oe.isEmpty()) return OperationResult.NOT_FOUND;
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
            } else {
                return OperationResult.CONFLICT;
            }
        } else {
            return OperationResult.CONFLICT;
        }
    }

    // markRecovered(id) -> OperationResult
    public OperationResult markRecovered(Long id) {
        if (id == null) return OperationResult.NOT_FOUND;
        Optional<GhostNetDataLayerModel> oe = repository.findById(id);
        if (oe.isEmpty()) return OperationResult.NOT_FOUND;
        GhostNetDataLayerModel entity = oe.get();
        if (entity.getStatus() == NetStatusDataLayerEnum.RECOVERY_PENDING) {
            entity.setStatus(NetStatusDataLayerEnum.RECOVERED);
            repository.save(entity);
            return OperationResult.OK;
        } else {
            return OperationResult.CONFLICT;
        }
    }

    // deleteById
    public OperationResult deleteById(Long id) {
        if (id == null) return OperationResult.NOT_FOUND;
        Optional<GhostNetDataLayerModel> oe = repository.findById(id);
        if (oe.isEmpty()) return OperationResult.NOT_FOUND;
        repository.deleteById(id);
        return OperationResult.OK;
    }
}
