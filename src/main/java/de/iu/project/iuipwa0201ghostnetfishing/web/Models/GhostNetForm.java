package de.iu.project.iuipwa0201ghostnetfishing.web.Models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public class GhostNetForm {
    @NotBlank(message = "Location ist erforderlich.")
    private String location;

    @NotNull(message = "Size ist erforderlich.")
    @PositiveOrZero(message = "Size muss >= 0 sein.")
    private Double size;

    private String personName;

    private String notes; // Field for recovery notes

    public GhostNetForm() {
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Double getSize() {
        return size;
    }

    public void setSize(Double size) {
        this.size = size;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
