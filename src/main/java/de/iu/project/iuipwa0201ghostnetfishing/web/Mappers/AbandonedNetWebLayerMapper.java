package de.iu.project.iuipwa0201ghostnetfishing.web.Mappers;

import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Models.AbandonedNetBusinessLayerModel;
import de.iu.project.iuipwa0201ghostnetfishing.web.Models.AbandonedNetWebLayerModel;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Konvertiert AbandonedNetBusinessLayerModel → AbandonedNetWebLayerModel.
 */
public final class AbandonedNetWebLayerMapper {

    private AbandonedNetWebLayerMapper() { throw new IllegalStateException("Utility class"); }

    /* Einzel-Objekt ------------------------------------------------------ */
    public static AbandonedNetWebLayerModel toWebModel(AbandonedNetBusinessLayerModel b) {
        if (b == null) return null;
        return new AbandonedNetWebLayerModel(
                b.getId(),
                b.getLocation(),
                b.getSize(),
                b.getStatus().name(),
                b.getCreatedAt(),
                b.getPerson() != null ? b.getPerson().getName() : null
        );
    }

    /* Liste -------------------------------------------------------------- */
    public static List<AbandonedNetWebLayerModel> toWebModelList(List<AbandonedNetBusinessLayerModel> list) {
        return list == null ? List.of()
                : list.stream()
                .map(AbandonedNetWebLayerMapper::toWebModel)
                .collect(Collectors.toList());
    }
}
