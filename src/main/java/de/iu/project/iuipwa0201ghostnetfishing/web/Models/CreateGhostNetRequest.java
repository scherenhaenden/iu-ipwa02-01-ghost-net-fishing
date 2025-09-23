package de.iu.project.iuipwa0201ghostnetfishing.web.Models;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateGhostNetRequest(
    @NotBlank String location,
    @NotNull @DecimalMin("0.0") Double size,
    String personName
) {}
