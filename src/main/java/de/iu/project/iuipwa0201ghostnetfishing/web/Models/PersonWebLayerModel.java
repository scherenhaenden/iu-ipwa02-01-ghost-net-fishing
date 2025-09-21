package de.iu.project.iuipwa0201ghostnetfishing.web.Models;

/**
 * Read-only representation of a person for the web layer.
 */
public record PersonWebLayerModel(
        Long   id,
        String name,
        String phoneNumber
) { }
