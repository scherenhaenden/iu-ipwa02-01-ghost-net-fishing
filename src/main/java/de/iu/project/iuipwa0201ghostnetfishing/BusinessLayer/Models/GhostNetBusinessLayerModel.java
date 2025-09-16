package de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Models;

import java.time.Instant;

public class GhostNetBusinessLayerModel {

    // --- Properties ---
    private Long id;
    private String location;
    private Double size;
    private NetStatusBusinessLayerEnum status;
    private PersonBusinessLayerModel recoveringPerson; // Usa el modelo de negocio de Person
    private Instant createdAt; // Usamos java.time.Instant para tipos de fecha modernos

    // --- Business Logic ---
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
        if (person == null) {
            throw new IllegalArgumentException("Person cannot be null.");
        }
        this.recoveringPerson = person;
        this.status = NetStatusBusinessLayerEnum.RECOVERY_PENDING;
    }

    /**
     * Marca la red como recuperada.
     * Regla de negocio: Solo se puede marcar como recuperada si está en RECOVERY_PENDING.
     */
    public void markAsRecovered() {
        if (this.status != NetStatusBusinessLayerEnum.RECOVERY_PENDING) {
            throw new IllegalStateException("Only a pending recovery can be marked as recovered. Current state: " + this.status);
        }
        this.status = NetStatusBusinessLayerEnum.RECOVERED;
    }


    // --- Constructors ---

    /**
     * Constructor sin argumentos.
     */
    public GhostNetBusinessLayerModel() {
    }

    /**
     * Constructor con todos los argumentos para facilitar la creación.
     */
    public GhostNetBusinessLayerModel(Long id, String location, Double size, NetStatusBusinessLayerEnum status, PersonBusinessLayerModel recoveringPerson, Instant createdAt) {
        this.id = id;
        this.location = location;
        this.size = size;
        this.status = status;
        this.recoveringPerson = recoveringPerson;
        this.createdAt = createdAt;
    }

    // --- Getters and Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public NetStatusBusinessLayerEnum getStatus() {
        return status;
    }

    public void setStatus(NetStatusBusinessLayerEnum status) {
        this.status = status;
    }

    public PersonBusinessLayerModel getRecoveringPerson() {
        return recoveringPerson;
    }

    public void setRecoveringPerson(PersonBusinessLayerModel recoveringPerson) {
        this.recoveringPerson = recoveringPerson;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
