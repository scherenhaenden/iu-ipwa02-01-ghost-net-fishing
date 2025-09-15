package de.iu.project.iuipwa0201ghostnetfishing.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;

/* AbandonedNet entity
   English replacement for former German-named entity. Represents an abandoned fishing net.
   Mapped to table ABANDONED_NET. Uses Jakarta Persistence annotations.
*/
@Entity
@Table(name = "ABANDONED_NET")
public class AbandonedNet implements Serializable {

    /* Primary key
       Auto-generated surrogate id for the AbandonedNet entity.
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
       Time when the net was first reported/created in the system.
    */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATED_AT", nullable = false)
    private Date createdAt = new Date();

    /* Current status
       Enum stored as string; reflects lifecycle stage of the net.
    */
    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false)
    private NetStatus status;

    /* Reporting/Recovery person
       Optional many-to-one relation to the Person who reported or recovered the net.
    */
    @ManyToOne
    @JoinColumn(name = "PERSON_ID")
    private Person person;

    /* Protected no-args constructor
       Required by JPA to instantiate the entity via reflection.
    */
    protected AbandonedNet() {
        // JPA
    }

    /* All-args constructor
       Useful for programmatic creation and tests. Includes optional person reference.
       createdAt is optional and set automatically when not provided.
    */
    public AbandonedNet(Long id, String location, Double size, NetStatus status, Person person) {
        this.id = id;
        this.location = location;
        this.size = size;
        this.status = status;
        this.person = person;
        this.createdAt = new Date();
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

    public NetStatus getStatus() {
        return status;
    }

    public void setStatus(NetStatus status) {
        this.status = status;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
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
