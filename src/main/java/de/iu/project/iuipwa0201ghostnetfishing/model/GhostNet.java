package de.iu.project.iuipwa0201ghostnetfishing.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;

/* GhostNet entity
   Represents a ghost (abandoned) fishing net record tracked by the system.
   Mapped to table GHOST_NET. Uses Jakarta Persistence annotations.
*/
@Entity
@Table(name = "GHOST_NET")
public class GhostNet implements Serializable {

    /* Primary key
       Auto-generated surrogate id for the GhostNet entity.
    */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /* Human-friendly name
       Short descriptive name for the net or report. Not nullable; limited length.
    */
    @Column(length = 120, nullable = false)
    private String name;

    /* Creation timestamp
       Stores when the record was created in the system. Not nullable.
    */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATED_AT", nullable = false)
    private Date createdAt = new Date();

    /* No-args constructor
       Required by JPA to instantiate entities via reflection.
       Kept public to preserve existing usage in the codebase.
    */
    public GhostNet() {}

    /* Convenience constructor
       Create a GhostNet with a name; createdAt is set to now.
    */
    public GhostNet(String name) {
        this.name = name;
        this.createdAt = new Date();
    }

    /* Getters and setters */
    /** Returns the ID. */
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    /** Returns the human-friendly name. */
    public String getName() { return name; }
    /** Sets the human-friendly name. */
    public void setName(String name) { this.name = name; }

    /** Returns the creation date/time. */
    public Date getCreatedAt() { return createdAt; }
    /** Sets the creation date/time. */
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    /* toString
       Concise, non-recursive representation useful for logging and debugging.
    */
    @Override
    public String toString() {
        return "GhostNet{id=" + id + ", name='" + name + "', createdAt=" + createdAt + "}";
    }
}
