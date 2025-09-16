package de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Models;

import java.time.Instant;

public class AbandonedNetBusinessLayerModel {
    private Long id;
    private String location;
    private Double size;
    private NetStatusBusinessLayerEnum status;
    private PersonBusinessLayerModel person; // Usa el modelo de negocio de Person
    private Instant createdAt; // Usamos java.time.Instant para tipos de fecha modernos

    // --- Lógica de Negocio ---
    // Aquí es donde viven las reglas de tu aplicación.

    /**
     * Verifica si a esta red se le puede asignar una persona para su recuperación.
     * Regla de negocio: Solo se puede asignar si el estado es REPORTED.
     * @return true si se puede asignar, de lo contrario false.
     */
    public boolean canBeAssigned() {
        return this.status == NetStatusBusinessLayerEnum.REPORTED;
    }

    /**
     * Asigna una persona a esta red y actualiza su estado.
     * @param person La persona a asignar.
     * @throws IllegalStateException si la red no está en un estado asignable.
     */
    public void assignTo(PersonBusinessLayerModel person) {
        if (!canBeAssigned()) {
            throw new IllegalStateException("Net cannot be assigned in its current state: " + this.status);
        }
        this.person = person;
        this.status = NetStatusBusinessLayerEnum.RECOVERY_PENDING;
    }

    // --- Constructores, Getters y Setters ---
    // (Generados por la IDE)

    public AbandonedNetBusinessLayerModel() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public Double getSize() { return size; }
    public void setSize(Double size) { this.size = size; }
    public NetStatusBusinessLayerEnum getStatus() { return status; }
    public void setStatus(NetStatusBusinessLayerEnum status) { this.status = status; }
    public PersonBusinessLayerModel getPerson() { return person; }
    public void setPerson(PersonBusinessLayerModel person) { this.person = person; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
