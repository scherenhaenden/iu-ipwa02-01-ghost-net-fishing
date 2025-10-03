// ...existing code...
package de.iu.project.iuipwa0201ghostnetfishing.web.Models;

import de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Models.NetStatusBusinessLayerEnum;

import java.time.Instant;

/**
 * Lightweight DTO used by Thymeleaf views to avoid passing business models/entities directly
 * and to provide a single property `recoveringPersonName` for templates.
 */
public class GhostNetViewModel {

    private Long id;
    private String location;
    private Double size;
    private NetStatusBusinessLayerEnum status;
    private Instant createdAt;
    private String recoveringPersonName;

    public GhostNetViewModel() {}

    public GhostNetViewModel(Long id, String location, Double size, NetStatusBusinessLayerEnum status, Instant createdAt, String recoveringPersonName) {
        this.id = id;
        this.location = location;
        this.size = size;
        this.status = status;
        this.createdAt = createdAt;
        this.recoveringPersonName = recoveringPersonName;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public Double getSize() { return size; }
    public void setSize(Double size) { this.size = size; }

    public NetStatusBusinessLayerEnum getStatus() { return status; }
    public void setStatus(NetStatusBusinessLayerEnum status) { this.status = status; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public String getRecoveringPersonName() { return recoveringPersonName; }
    public void setRecoveringPersonName(String recoveringPersonName) { this.recoveringPersonName = recoveringPersonName; }
}

