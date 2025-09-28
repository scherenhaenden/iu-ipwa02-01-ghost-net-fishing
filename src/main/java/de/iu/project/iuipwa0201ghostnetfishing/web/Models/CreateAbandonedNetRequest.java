package de.iu.project.iuipwa0201ghostnetfishing.web.Models;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for creating an AbandonedNet (API).
 */
public class CreateAbandonedNetRequest {

    @NotBlank(message = "location must not be blank")
    @Size(max = 255)
    private String location;

    @NotNull(message = "size is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "size must be >= 0")
    private Double size;

    @Size(max = 120)
    private String personName; // optional

    public CreateAbandonedNetRequest() { }

    public CreateAbandonedNetRequest(String location, Double size, String personName) {
        this.location = location;
        this.size = size;
        this.personName = personName;
    }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public Double getSize() { return size; }
    public void setSize(Double size) { this.size = size; }

    public String getPersonName() { return personName; }
    public void setPersonName(String personName) { this.personName = personName; }
}

