package de.iu.project.iuipwa0201ghostnetfishing.web.Models;

import jakarta.validation.constraints.NotBlank;

public class ReserveForm {

    @NotBlank(message = "Name ist erforderlich.")
    private String personName;

    public ReserveForm() {
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    // Helper method to get trimmed name for validation
    public String getTrimmedPersonName() {
        return personName != null ? personName.trim() : null;
    }
}
