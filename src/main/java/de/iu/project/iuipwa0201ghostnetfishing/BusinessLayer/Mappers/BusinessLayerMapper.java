package de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Mappers;

import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Models.AbandonedNetBusinessLayerModel;
import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Models.NetStatusBusinessLayerEnum;
import de.iu.project.iuipwa0201ghostnetfishing.DatabaseLayer.Models.AbandonedNetDataLayerModel;
import de.iu.project.iuipwa0201ghostnetfishing.DatabaseLayer.Models.NetStatusDataLayerEnum;
import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Models.GhostNetBusinessLayerModel;
import de.iu.project.iuipwa0201ghostnetfishing.DatabaseLayer.Models.GhostNetDataLayerModel;

import java.util.List;
import java.util.stream.Collectors;

public class BusinessLayerMapper {

    // --- Mapeo de Entidad de DB -> Modelo de Negocio ---
    public static AbandonedNetBusinessLayerModel toBusinessModel(AbandonedNetDataLayerModel entity) {
        if (entity == null) {
            return null;
        }
        AbandonedNetBusinessLayerModel businessModel = new AbandonedNetBusinessLayerModel();
        businessModel.setId(entity.getId());
        businessModel.setLocation(entity.getLocation());
        businessModel.setSize(entity.getSize());
        businessModel.setStatus(NetStatusBusinessLayerEnum.valueOf(entity.getStatus().name()));
        businessModel.setCreatedAt(entity.getCreatedAt().toInstant());
        // Aquí mapearíamos la persona si fuera necesario
        return businessModel;
    }

    public static List<AbandonedNetBusinessLayerModel> toBusinessModelList(List<AbandonedNetDataLayerModel> entities) {
        return entities.stream()
                .map(BusinessLayerMapper::toBusinessModel)
                .collect(Collectors.toList());
    }

    // --- Mapeo de Modelo de Negocio -> Entidad de DB ---
    public static AbandonedNetDataLayerModel toEntity(AbandonedNetBusinessLayerModel businessModel) {
        if (businessModel == null) {
            return null;
        }
        // Nota: Este constructor es un ejemplo. Necesitarías uno adecuado en tu entidad.
        AbandonedNetDataLayerModel entity = new AbandonedNetDataLayerModel(
                businessModel.getId(),
                businessModel.getLocation(),
                businessModel.getSize(),
                NetStatusDataLayerEnum.valueOf(businessModel.getStatus().name()),
                null // La persona se manejaría por separado
        );
        return entity;
    }

    // --- Mapeo de Entidad de DB -> Modelo de Negocio para GhostNet ---
    public static GhostNetBusinessLayerModel toBusinessModel(GhostNetDataLayerModel entity) {
        if (entity == null) {
            return null;
        }
        GhostNetBusinessLayerModel businessModel = new GhostNetBusinessLayerModel();
        businessModel.setId(entity.getId());
        businessModel.setLocation(entity.getLocation());
        businessModel.setSize(entity.getSize());
        businessModel.setStatus(NetStatusBusinessLayerEnum.valueOf(entity.getStatus().name()));
        businessModel.setCreatedAt(entity.getCreatedAt().toInstant());
        // Aquí mapearíamos la persona si fuera necesario
        return businessModel;
    }

    public static List<GhostNetBusinessLayerModel> toBusinessModelList(List<GhostNetDataLayerModel> entities) {
        return entities.stream()
                .map(BusinessLayerMapper::toBusinessModel)
                .collect(Collectors.toList());
    }

    // --- Mapeo de Modelo de Negocio -> Entidad de DB para GhostNet ---
    public static GhostNetDataLayerModel toEntity(GhostNetBusinessLayerModel businessModel) {
        if (businessModel == null) {
            return null;
        }
        // Nota: Este constructor es un ejemplo. Necesitarías uno adecuado en tu entidad.
        GhostNetDataLayerModel entity = new GhostNetDataLayerModel(
                businessModel.getId(),
                businessModel.getLocation(),
                businessModel.getSize(),
                NetStatusDataLayerEnum.valueOf(businessModel.getStatus().name()),
                null // La persona se manejaría por separado
        );
        return entity;
    }
}
