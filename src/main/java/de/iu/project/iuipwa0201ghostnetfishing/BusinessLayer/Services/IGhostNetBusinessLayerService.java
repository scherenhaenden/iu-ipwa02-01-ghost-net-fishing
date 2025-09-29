package de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Services;

import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Models.GhostNetBusinessLayerModel;
import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Models.NetStatusBusinessLayerEnum;

import java.util.List;
import java.util.Optional;

public interface IGhostNetBusinessLayerService {
    List<GhostNetBusinessLayerModel> findAll();

    List<GhostNetBusinessLayerModel> findByStatus(NetStatusBusinessLayerEnum status);

    GhostNetBusinessLayerModel save(GhostNetBusinessLayerModel net);

    void deleteById(Long id);

    GhostNetBusinessLayerModel findByIdOrThrow(Long id);

    Optional<GhostNetBusinessLayerModel> findById(Long id);
}
