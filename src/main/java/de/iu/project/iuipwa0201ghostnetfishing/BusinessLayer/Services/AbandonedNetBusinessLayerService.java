package de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Services;

import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Mappers.BusinessLayerMapper;
import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Models.AbandonedNetBusinessLayerModel;
import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Models.NetStatusBusinessLayerEnum;
import de.iu.project.iuipwa0201ghostnetfishing.DatabaseLayer.Models.AbandonedNetDataLayerModel;
import de.iu.project.iuipwa0201ghostnetfishing.DatabaseLayer.Models.NetStatusDataLayerEnum;
import de.iu.project.iuipwa0201ghostnetfishing.DatabaseLayer.Repositories.AbandonedNetDataLayerModelRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class AbandonedNetBusinessLayerService implements IAbandonedNetBusinessLayerService {
    private final AbandonedNetDataLayerModelRepository repository;

    // Dependency injection via constructor (best practice)
    public AbandonedNetBusinessLayerService(AbandonedNetDataLayerModelRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true) // Read-only transaction is more efficient
    public List<AbandonedNetBusinessLayerModel> getAllNetsNewestFirst() {
        // 1. Retrieve entities from the data layer
        List<AbandonedNetDataLayerModel> entities = repository.findAllByOrderByCreatedAtDesc();
        // 2. Map to business models and return
        return BusinessLayerMapper.toBusinessModelList(entities);
    }

    @Override
    @Transactional
    public AbandonedNetBusinessLayerModel save(AbandonedNetBusinessLayerModel netBusinessModel) {
        // 1. Map the business model to a data layer entity
        AbandonedNetDataLayerModel entityToSave = BusinessLayerMapper.toEntity(netBusinessModel);
        // 2. Save the entity using the repository
        AbandonedNetDataLayerModel savedEntity = repository.save(entityToSave);
        // 3. Map the saved entity back to a business model and return it
        return BusinessLayerMapper.toBusinessModel(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AbandonedNetBusinessLayerModel> findByStatus(NetStatusBusinessLayerEnum status) {
        // 1. Convert the business enum to the data layer enum
        NetStatusDataLayerEnum dataLayerStatus = NetStatusDataLayerEnum.valueOf(status.name());
        // 2. Retrieve the entities
        List<AbandonedNetDataLayerModel> entities = repository.findByStatus(dataLayerStatus);
        // 3. Map to business models
        return BusinessLayerMapper.toBusinessModelList(entities);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
