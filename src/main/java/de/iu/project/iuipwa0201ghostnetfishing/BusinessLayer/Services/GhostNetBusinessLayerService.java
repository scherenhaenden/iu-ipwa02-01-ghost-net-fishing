package de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Services;

import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Mappers.BusinessLayerMapper;
import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Models.GhostNetBusinessLayerModel;
import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Models.NetStatusBusinessLayerEnum;
import de.iu.project.iuipwa0201ghostnetfishing.DatabaseLayer.Models.GhostNetDataLayerModel;
import de.iu.project.iuipwa0201ghostnetfishing.DatabaseLayer.Models.NetStatusDataLayerEnum;
import de.iu.project.iuipwa0201ghostnetfishing.DatabaseLayer.Repositories.GhostNetDataLayerModelRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GhostNetBusinessLayerService implements IGhostNetBusinessLayerService {

    private final GhostNetDataLayerModelRepository repository;

    // Dependency injection via constructor (best practice)
    public GhostNetBusinessLayerService(GhostNetDataLayerModelRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true) // Read-only transaction is more efficient
    public List<GhostNetBusinessLayerModel> findAll() {
        List<GhostNetDataLayerModel> entities = repository.findAll();
        return BusinessLayerMapper.toGhostNetBusinessModelList(entities);
    }

    @Override
    @Transactional
    public GhostNetBusinessLayerModel save(GhostNetBusinessLayerModel netBusinessModel) {
        GhostNetDataLayerModel entityToSave = BusinessLayerMapper.toEntity(netBusinessModel);
        GhostNetDataLayerModel savedEntity = repository.save(entityToSave);
        return BusinessLayerMapper.toBusinessModel(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GhostNetBusinessLayerModel> findByStatus(NetStatusBusinessLayerEnum status) {
        // Convert business enum to data-layer enum
        NetStatusDataLayerEnum dataLayerStatus = NetStatusDataLayerEnum.valueOf(status.name());
        // Assuming this method exists in the repo
        List<GhostNetDataLayerModel> entities = repository.findByStatus(dataLayerStatus);
        return BusinessLayerMapper.toGhostNetBusinessModelList(entities);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
