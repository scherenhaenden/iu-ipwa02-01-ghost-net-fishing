package de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Services;

import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Models.GhostNetBusinessLayerModel;
import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Models.NetStatusBusinessLayerEnum;
import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Models.PersonBusinessLayerModel;

import java.util.List;
import java.util.Optional;

public interface IGhostNetBusinessLayerService {
    List<GhostNetBusinessLayerModel> findAll();

    List<GhostNetBusinessLayerModel> findByStatus(NetStatusBusinessLayerEnum status);

    GhostNetBusinessLayerModel save(GhostNetBusinessLayerModel net);

    void deleteById(Long id);

    Optional<GhostNetBusinessLayerModel> findById(Long id);

    // New for US2: reserve a net by id with a person model, returns OperationResult (OK / NOT_FOUND / CONFLICT)
    de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Services.OperationResult reserve(Long id, PersonBusinessLayerModel person);

    // New for US3: mark a net as recovered, returns OperationResult (OK / NOT_FOUND / CONFLICT)
    de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Services.OperationResult recover(Long id);

    // New for US4: mark a net as missing, returns OperationResult (OK / NOT_FOUND / CONFLICT)
    de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Services.OperationResult markMissing(Long id);
}
