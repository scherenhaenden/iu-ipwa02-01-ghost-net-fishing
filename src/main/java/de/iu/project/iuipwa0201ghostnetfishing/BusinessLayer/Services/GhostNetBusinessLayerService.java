package de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Services;

import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Mappers.GhostNetBusinessLayerMapper;
import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Models.GhostNetBusinessLayerModel;
import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Models.NetStatusBusinessLayerEnum;
import de.iu.project.iuipwa0201ghostnetfishing.DatabaseLayer.Models.GhostNetDataLayerModel;
import de.iu.project.iuipwa0201ghostnetfishing.DatabaseLayer.Models.NetStatusDataLayerEnum;
import de.iu.project.iuipwa0201ghostnetfishing.DatabaseLayer.Repositories.GhostNetDataLayerModelRepository;
import de.iu.project.iuipwa0201ghostnetfishing.exceptions.ResourceNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GhostNetBusinessLayerService implements IGhostNetBusinessLayerService {

    private final GhostNetDataLayerModelRepository repository;
    private final GhostNetBusinessLayerMapper mapper;

    // Dependency injection via constructor (best practice)
    public GhostNetBusinessLayerService(GhostNetDataLayerModelRepository repository, GhostNetBusinessLayerMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    @Transactional(readOnly = true) // Read-only transaction is more efficient
    public List<GhostNetBusinessLayerModel> findAll() {
        //List<GhostNetDataLayerModel> entities = repository.findAll();
        //return mapper.toBusinessModelList(entities);
        return List.of();
    }

    @Override
    @Transactional
    public GhostNetBusinessLayerModel save(GhostNetBusinessLayerModel netBusinessModel) {
        GhostNetDataLayerModel entityToSave = mapper.toEntity(netBusinessModel);
        GhostNetDataLayerModel savedEntity = repository.save(entityToSave);
        return mapper.toBusinessModel(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GhostNetBusinessLayerModel> findByStatus(NetStatusBusinessLayerEnum status) {
        // Convert business enum to data-layer enum
        NetStatusDataLayerEnum dataLayerStatus = NetStatusDataLayerEnum.valueOf(status.name());
        // Assuming this method exists in the repo
        List<GhostNetDataLayerModel> entities = repository.findByStatus(dataLayerStatus);
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
}
