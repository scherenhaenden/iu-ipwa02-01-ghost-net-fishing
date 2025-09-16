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

    // Inyección de dependencias por constructor (mejor práctica)
    public AbandonedNetBusinessLayerService(AbandonedNetDataLayerModelRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true) // Transacción de solo lectura es más eficiente
    public List<AbandonedNetBusinessLayerModel> getAllNetsNewestFirst() {
        // 1. Obtener entidades de la capa de datos
        List<AbandonedNetDataLayerModel> entities = repository.findAllByOrderByCreatedAtDesc();
        // 2. Mapear a modelos de negocio y devolver
        return BusinessLayerMapper.toBusinessModelList(entities);
    }

    @Override
    @Transactional
    public AbandonedNetBusinessLayerModel save(AbandonedNetBusinessLayerModel netBusinessModel) {
        // 1. Mapear el modelo de negocio a una entidad de la capa de datos
        AbandonedNetDataLayerModel entityToSave = BusinessLayerMapper.toEntity(netBusinessModel);
        // 2. Guardar la entidad usando el repositorio
        AbandonedNetDataLayerModel savedEntity = repository.save(entityToSave);
        // 3. Mapear la entidad guardada de vuelta a un modelo de negocio y devolverla
        return BusinessLayerMapper.toBusinessModel(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AbandonedNetBusinessLayerModel> findByStatus(NetStatusBusinessLayerEnum status) {
        // 1. Convertir el enum de negocio al enum de la capa de datos
        NetStatusDataLayerEnum dataLayerStatus = NetStatusDataLayerEnum.valueOf(status.name());
        // 2. Obtener las entidades
        List<AbandonedNetDataLayerModel> entities = repository.findByStatus(dataLayerStatus);
        // 3. Mapear a modelos de negocio
        return BusinessLayerMapper.toBusinessModelList(entities);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
