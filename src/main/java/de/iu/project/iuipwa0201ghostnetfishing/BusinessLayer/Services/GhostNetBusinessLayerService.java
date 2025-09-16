package de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Services;

import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Mappers.BusinessLayerMapper;
import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Models.GhostNetBusinessLayerModel;
import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Models.NetStatusBusinessLayerEnum;
import de.iu.project.iuipwa0201ghostnetfishing.DatabaseLayer.Models.GhostNetDataLayerModel;
import de.iu.project.iuipwa0201ghostnetfishing.DatabaseLayer.Repositories.GhostNetDataLayerModelRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class GhostNetBusinessLayerService {

    private final GhostNetDataLayerModelRepository repository;

    // Inyección de dependencias por constructor (mejor práctica)
    public GhostNetBusinessLayerService(GhostNetDataLayerModelRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true) // Transacción de solo lectura es más eficiente
    public List<GhostNetBusinessLayerModel> findAll() {
        List<GhostNetDataLayerModel> entities = repository.findAll();
        return BusinessLayerMapper.toBusinessModelList(entities);
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
        NetStatusBusinessLayerEnum dataLayerStatus = NetStatusBusinessLayerEnum.valueOf(status.name());
        List<GhostNetDataLayerModel> entities = repository.findByStatus(dataLayerStatus); // Asumiendo que este método existe en el repo
        return BusinessLayerMapper.toBusinessModelList(entities);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
