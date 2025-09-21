package de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Services;

import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Models.GhostNetBusinessLayerModel;
import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Models.NetStatusBusinessLayerEnum;

import java.util.List;

public interface IGhostNetBusinessLayerService {
    List<GhostNetBusinessLayerModel> findAll();

    List<GhostNetBusinessLayerModel> findByStatus(NetStatusBusinessLayerEnum status);

    GhostNetBusinessLayerModel save(GhostNetBusinessLayerModel net);

    void deleteById(Long id);
}
