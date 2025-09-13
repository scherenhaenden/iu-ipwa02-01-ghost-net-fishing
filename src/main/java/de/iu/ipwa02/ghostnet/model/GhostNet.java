package de.iu.ipwa02.ghostnet.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Ghost Net entity representing a lost or abandoned fishing net
 */
@Entity
@Table(name = "ghost_nets")
@NamedQueries({
    @NamedQuery(name = "GhostNet.findAll", query = "SELECT g FROM GhostNet g"),
    @NamedQuery(name = "GhostNet.findByStatus", query = "SELECT g FROM GhostNet g WHERE g.status = :status")
})
public class GhostNet implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 2, max = 100)
    @Column(name = "location")
    private String location;

    @NotNull
    @Column(name = "latitude")
    private Double latitude;

    @NotNull
    @Column(name = "longitude")
    private Double longitude;

    @NotNull
    @Size(min = 2, max = 50)
    @Column(name = "size_estimate")
    private String sizeEstimate;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private NetStatus status;

    @Column(name = "reported_date")
    private LocalDateTime reportedDate;

    @Column(name = "recovery_date")
    private LocalDateTime recoveryDate;

    @Size(max = 500)
    @Column(name = "notes")
    private String notes;

    // Constructors
    public GhostNet() {
        this.reportedDate = LocalDateTime.now();
        this.status = NetStatus.REPORTED;
    }

    public GhostNet(String location, Double latitude, Double longitude, String sizeEstimate) {
        this();
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.sizeEstimate = sizeEstimate;
    }

    // Getters and Setters
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

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getSizeEstimate() {
        return sizeEstimate;
    }

    public void setSizeEstimate(String sizeEstimate) {
        this.sizeEstimate = sizeEstimate;
    }

    public NetStatus getStatus() {
        return status;
    }

    public void setStatus(NetStatus status) {
        this.status = status;
    }

    public LocalDateTime getReportedDate() {
        return reportedDate;
    }

    public void setReportedDate(LocalDateTime reportedDate) {
        this.reportedDate = reportedDate;
    }

    public LocalDateTime getRecoveryDate() {
        return recoveryDate;
    }

    public void setRecoveryDate(LocalDateTime recoveryDate) {
        this.recoveryDate = recoveryDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        GhostNet ghostNet = (GhostNet) obj;
        return id != null && id.equals(ghostNet.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "GhostNet{" +
                "id=" + id +
                ", location='" + location + '\'' +
                ", status=" + status +
                '}';
    }
}