package de.iu.project.iuipwa0201ghostnetfishing.web.Mappers;

import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Models.GhostNetBusinessLayerModel;
import de.iu.project.iuipwa0201ghostnetfishing.web.Models.GhostNetWebLayerModel;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Converts GhostNetBusinessLayerModel → GhostNetWebLayerModel.
 */
public final class GhostNetWebLayerMapper {

    private GhostNetWebLayerMapper() { throw new IllegalStateException("Utility class"); }

    /* Single object ------------------------------------------------------ */
    public static GhostNetWebLayerModel toWebModel(GhostNetBusinessLayerModel b) {
        if (b == null) return null;
        return new GhostNetWebLayerModel(
                b.getId(),
                b.getLocation(),
                b.getSize(),
                b.getStatus().name(),
                b.getCreatedAt(),
                b.getRecoveringPerson() != null ? b.getRecoveringPerson().getName() : null
        );
    }

    /* List -------------------------------------------------------------- */
    public static List<GhostNetWebLayerModel> toWebModelList(List<GhostNetBusinessLayerModel> list) {
        return list == null ? List.of()
                : list.stream()
                .map(GhostNetWebLayerMapper::toWebModel)
                .collect(Collectors.toList());
    }
}
