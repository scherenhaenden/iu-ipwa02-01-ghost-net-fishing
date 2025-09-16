package de.iu.project.iuipwa0201ghostnetfishing.web.Models;

import java.time.Instant;

/**
 * Read-only-DTO eines AbandonedNet für den Web-Layer.
 */
public record AbandonedNetWebLayerModel(
        Long   id,
        String location,
        Double size,
        String status,
        Instant createdAt,
        String personName           // meldende/bergende Person (Name)
) { }