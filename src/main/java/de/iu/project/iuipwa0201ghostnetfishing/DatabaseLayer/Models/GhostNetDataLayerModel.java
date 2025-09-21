package de.iu.project.iuipwa0201ghostnetfishing.DatabaseLayer.Models;

import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/* GhostNet entity
   Represents a ghost (abandoned) fishing net record tracked by the system.
   Mapped to table GHOST_NET. Uses Jakarta Persistence annotations.
*/
@Entity
@Table(name = "GHOST_NET")
public class GhostNetDataLayerModel implements Serializable {

    /* Primary key
       Auto-generated surrogate id for the GhostNet entity.
    */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /* GPS location
       Stores a textual GPS coordinate or location description. Not null.
    */
    @Column(name = "LOCATION", nullable = false)
    private String location;

    /* Area size
       Size of the net in square meters. Not null.
    */
    @Column(name = "SIZE", nullable = false)
    private Double size;

    /* Creation timestamp
       Stores when the record was created in the system. Not nullable.
    */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATED_AT", nullable = false)
    private Date createdAt = new Date();

    /* Current status
       Enum stored as string; reflects lifecycle stage of the net.
    */
    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false)
    private NetStatusDataLayerEnum status;

    /* Reporting/Recovery person
       Optional many-to-one relation to the Person who reported or recovered the net.
    */
    @ManyToOne
    @JoinColumn(name = "PERSON_ID")
    private PersonDataLayerModel person;

    /* No-args constructor
       Required by JPA to instantiate entities via reflection.
       Kept public to preserve existing usage in the codebase.
    */
    protected GhostNetDataLayerModel() {
        // JPA
    }

    /* Convenience constructors
       Create a GhostNet with location, size, status, and person; createdAt is set to now.
    */
    public GhostNetDataLayerModel(String location, Double size, NetStatusDataLayerEnum status, PersonDataLayerModel person) {
        this.location = location;
        this.size = size;
        this.status = status;
        this.person = person;
        this.createdAt = new Date();
    }

    public GhostNetDataLayerModel(Long id, String location, Double size, NetStatusDataLayerEnum status, PersonDataLayerModel person) {
        this.id = id;
        this.location = location;
        this.size = size;
        this.status = status;
        this.person = person;
        this.createdAt = new Date();
    }

    public GhostNetDataLayerModel(String location, Double size, NetStatusDataLayerEnum status, LocalDateTime createdAt, PersonDataLayerModel person) {
        this.location = location;
        this.size = size;
        this.status = status;
        this.person = person;
        this.createdAt = Date.from(createdAt.atZone(ZoneId.systemDefault()).toInstant());
    }

    /* Getters and setters */
    /** Returns the ID. */
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    /** Returns the GPS location. */
    public String getLocation() { return location; }
    /** Sets the GPS location. */
    public void setLocation(String location) { this.location = location; }

    /** Returns the area size. */
    public Double getSize() { return size; }
    /** Sets the area size. */
    public void setSize(Double size) { this.size = size; }

    /** Returns the creation date/time. */
    public Date getCreatedAt() { return createdAt; }
    /** Sets the creation date/time. */
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    /** Returns the current status. */
    public NetStatusDataLayerEnum getStatus() { return status; }
    /** Sets the current status. */
    public void setStatus(NetStatusDataLayerEnum status) { this.status = status; }

    /** Returns the reporting/recovery person. */
    public PersonDataLayerModel getPerson() { return person; }
    /** Sets the reporting/recovery person. */
    public void setPerson(PersonDataLayerModel person) { this.person = person; }

    /* toString
       Concise, non-recursive representation useful for logging and debugging.
    */
    @Override
    public String toString() {
        return "GhostNetDataLayerModel{" +
                "id=" + id +
                ", location='" + location + '\'' +
                ", size=" + size +
                ", createdAt=" + createdAt +
                ", status=" + status +
                ", person=" + (person != null ? person.getId() : null) +
                '}';
    }
}
