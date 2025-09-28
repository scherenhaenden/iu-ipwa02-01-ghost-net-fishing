package de.iu.project.iuipwa0201ghostnetfishing.DatabaseLayer.Models;

import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.PastOrPresent;

import java.io.Serializable;
import java.util.Date;

/* AbandonedNet entity
   English replacement for former German-named entity. Represents an abandoned fishing net.
   Mapped to table ABANDONED_NET. Uses Jakarta Persistence annotations.
*/
@Entity
@Table(name = "ABANDONED_NET", indexes = {
        @Index(name = "idx_abandonednet_status", columnList = "STATUS"),
        @Index(name = "idx_abandonednet_created_at", columnList = "CREATED_AT"),
        @Index(name = "idx_abandonednet_location", columnList = "LOCATION")
})
public class AbandonedNetDataLayerModel implements Serializable {

    /* Primary key
       Auto-generated surrogate id for the AbandonedNet entity.
    */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /* GPS location
       Stores a textual GPS coordinate or location description. Not null.
    */
    @NotBlank
    @Size(max = 255)
    @Column(name = "LOCATION", nullable = false, length = 255)
    private String location;

    /* Area size
       Size of the net in square meters. Not null.
    */
    @NotNull
    @PositiveOrZero
    @Column(name = "SIZE", nullable = false)
    private Double size;

    /* Creation timestamp
       Time when the net was first reported/created in the system.
    */
    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @PastOrPresent
    @Column(name = "CREATED_AT", nullable = false)
    private Date createdAt = new Date();

    /* Current status
       Enum stored as string; reflects lifecycle stage of the net.
    */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false)
    private NetStatusDataLayerEnum status;

    /* Reporting/Recovery person
       Optional many-to-one relation to the Person who reported or recovered the net.
    */
    @ManyToOne(optional = true, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "PERSON_ID")
    private PersonDataLayerModel person;

    /* Protected no-args constructor
       Required by JPA to instantiate the entity via reflection.
    */
    protected AbandonedNetDataLayerModel() {
        // JPA
    }

    /* All-args constructor
       Useful for programmatic creation and tests. Includes optional person reference.
       createdAt is optional and set automatically when not provided.
    */
    public AbandonedNetDataLayerModel(Long id, String location, Double size, NetStatusDataLayerEnum status, PersonDataLayerModel person) {
        this.id = id;
        this.location = location;
        this.size = size;
        this.status = status;
        this.person = person;
        this.createdAt = new Date();
    }

    /**
     * New constructor which accepts an explicit createdAt (may be null).
     * This allows mappers to preserve null createdAt values for tests.
     */
    public AbandonedNetDataLayerModel(Long id, String location, Double size, NetStatusDataLayerEnum status, Date createdAt, PersonDataLayerModel person) {
        this.id = id;
        this.location = location;
        this.size = size;
        this.status = status;
        this.person = person;
        this.createdAt = createdAt;
    }

    /* Convenience constructor with LocalDateTime
       Accepts a LocalDateTime for createdAt and converts it to java.util.Date for JPA compatibility.
       This constructor is convenient for services that use java.time types.
    */
    public AbandonedNetDataLayerModel(String location, Double size, NetStatusDataLayerEnum status, java.time.LocalDateTime createdAt, PersonDataLayerModel person) {
        this.location = location;
        this.size = size;
        this.status = status;
        this.person = person;
        this.createdAt = Date.from(createdAt.atZone(java.time.ZoneId.systemDefault()).toInstant());
    }

    /* Getters and setters */
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

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public NetStatusDataLayerEnum getStatus() {
        return status;
    }

    public void setStatus(NetStatusDataLayerEnum status) {
        this.status = status;
    }

    public PersonDataLayerModel getPerson() {
        return person;
    }

    public void setPerson(PersonDataLayerModel person) {
        this.person = person;
    }

    /* toString
       Provides a concise string representation without causing recursion.
    */
    @Override
    public String toString() {
        String p = (person == null) ? "null" : "Person{id=" + person.getId() + ", name='" + person.getName() + "'}";
        return "AbandonedNet{id=" + id + ", location='" + location + "', size=" + size + ", createdAt=" + createdAt + ", status=" + status + ", person=" + p + "}";
    }
}
