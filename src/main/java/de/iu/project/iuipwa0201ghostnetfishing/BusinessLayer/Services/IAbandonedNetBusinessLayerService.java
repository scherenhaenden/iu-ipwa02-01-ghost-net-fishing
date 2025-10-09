package de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Services;


import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Models.AbandonedNetBusinessLayerModel;
import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Models.NetStatusBusinessLayerEnum;

import java.util.List;

public interface IAbandonedNetBusinessLayerService {

    List<AbandonedNetBusinessLayerModel> getAllNetsNewestFirst();

    List<AbandonedNetBusinessLayerModel> findByStatus(NetStatusBusinessLayerEnum status);

    AbandonedNetBusinessLayerModel save(AbandonedNetBusinessLayerModel net);

    /**
     * Finds an abandoned net by id or throws ResourceNotFoundException.
     */
    AbandonedNetBusinessLayerModel findById(Long id);

    void deleteById(Long id);
}
