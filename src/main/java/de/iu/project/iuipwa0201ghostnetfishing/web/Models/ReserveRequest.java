package de.iu.project.iuipwa0201ghostnetfishing.web.Models;

import jakarta.validation.constraints.NotBlank;

public record ReserveRequest(@NotBlank String personName) {}
