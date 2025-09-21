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

    // --- DB Entity -> Business Model ---
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
        // Here we would map the person if needed
        return businessModel;
    }

    public static List<AbandonedNetBusinessLayerModel> toBusinessModelList(List<AbandonedNetDataLayerModel> entities) {
        return entities.stream()
                .map(BusinessLayerMapper::toBusinessModel)
                .collect(Collectors.toList());
    }

    // --- Business Model -> DB Entity ---
    public static AbandonedNetDataLayerModel toEntity(AbandonedNetBusinessLayerModel businessModel) {
        if (businessModel == null) {
            return null;
        }
        // Note: This constructor is an example. You would need an appropriate one on your entity.
        AbandonedNetDataLayerModel entity = new AbandonedNetDataLayerModel(
                businessModel.getId(),
                businessModel.getLocation(),
                businessModel.getSize(),
                NetStatusDataLayerEnum.valueOf(businessModel.getStatus().name()),
                null // Person would be handled separately
        );
        return entity;
    }

    // --- DB Entity -> Business Model for GhostNet ---
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
        // Here we would map the person if needed
        return businessModel;
    }

    public static List<GhostNetBusinessLayerModel> toBusinessModelList(List<GhostNetDataLayerModel> entities) {
        return entities.stream()
                .map(BusinessLayerMapper::toBusinessModel)
                .collect(Collectors.toList());
    }

    // --- Business Model -> DB Entity for GhostNet ---
    public static GhostNetDataLayerModel toEntity(GhostNetBusinessLayerModel businessModel) {
        if (businessModel == null) {
            return null;
        }
        // Note: This constructor is an example. You would need an appropriate one on your entity.
        GhostNetDataLayerModel entity = new GhostNetDataLayerModel(
                businessModel.getId(),
                businessModel.getLocation(),
                businessModel.getSize(),
                NetStatusDataLayerEnum.valueOf(businessModel.getStatus().name()),
                null // Person would be handled separately
        );
        return entity;
    }
}
