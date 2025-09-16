package de.iu.project.iuipwa0201ghostnetfishing.web.Models;

/**
 * Read-only-Darstellung einer Person für den Web-Layer.
 */
public record PersonWebLayerModel(
        Long   id,
        String name,
        String phoneNumber
) { }
