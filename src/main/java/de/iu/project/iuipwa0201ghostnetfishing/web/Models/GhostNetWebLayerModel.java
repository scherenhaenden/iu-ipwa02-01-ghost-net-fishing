package de.iu.project.iuipwa0201ghostnetfishing.web.Models;

import java.time.Instant;

/**
 * Read-only DTO of a GhostNet for lists, detail pages, or REST.
 * recoveringPersonName suffices for most UI cases; if you need the complete person,
 * replace the type with PersonWebLayerModel.
 */
public record GhostNetWebLayerModel(
        Long   id,
        String location,
        Double size,
        String status,              // Enum as string
        Instant createdAt,
        String recoveringPersonName // Only the person's name
) { }