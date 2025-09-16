package de.iu.project.iuipwa0201ghostnetfishing.web.Models;

import java.time.Instant;

/**
 * Read-only-DTO eines GhostNets für Listen, Detailseiten oder REST.
 * recoveringPersonName genügt den meisten UI-Fällen; wenn du die
 * komplette Person brauchst, ersetze den Typ durch PersonWebLayerModel.
 */
public record GhostNetWebLayerModel(
        Long   id,
        String location,
        Double size,
        String status,              // Enum als String
        Instant createdAt,
        String recoveringPersonName // nur der Name der Person
) { }