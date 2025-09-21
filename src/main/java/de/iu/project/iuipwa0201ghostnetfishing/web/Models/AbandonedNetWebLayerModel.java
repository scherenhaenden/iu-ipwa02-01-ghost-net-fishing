package de.iu.project.iuipwa0201ghostnetfishing.web.Models;

import java.time.Instant;

/**
 * Read-only DTO of an AbandonedNet for the web layer.
 */
public record AbandonedNetWebLayerModel(
        Long   id,
        String location,
        Double size,
        String status,
        Instant createdAt,
        String personName           // Reporting/recovering person (name)
) { }