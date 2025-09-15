package de.iu.project.iuipwa0201ghostnetfishing.model;

import jakarta.persistence.*;
import java.io.Serializable;

/* indo docs: AbandonedNet entity
   English replacement for former German-named entity. Represents an abandoned fishing net.
   Mapped to table ABANDONED_NET. Uses Jakarta Persistence annotations.
*/
@Entity
@Table(name = "ABANDONED_NET")
public class AbandonedNet implements Serializable {

    /* indo docs: Primary key
       Auto-generated surrogate id for the AbandonedNet entity.
    */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /* indo docs: GPS location
       Stores a textual GPS coordinate or location description. Not null.
    */
    @Column(name = "LOCATION", nullable = false)
    private String location;

    /* indo docs: Area size
       Size of the net in square meters. Not null.
    */
    @Column(name = "SIZE", nullable = false)
    private Double size;

    /* indo docs: Current status
       Enum stored as string; reflects lifecycle stage of the net.
    */
    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false)
    private NetStatus status;

    /* indo docs: Reporting/Recovery person
       Optional many-to-one relation to the Person who reported or recovered the net.
    */
    @ManyToOne
    @JoinColumn(name = "PERSON_ID")
    private Person person;

    /* indo docs: Protected no-args constructor
       Required by JPA to instantiate the entity via reflection.
    */
    protected AbandonedNet() {
        // JPA
    }

    /* indo docs: All-args constructor
       Useful for programmatic creation and tests. Includes optional person reference.
    */
    public AbandonedNet(Long id, String location, Double size, NetStatus status, Person person) {
        this.id = id;
        this.location = location;
        this.size = size;
        this.status = status;
        this.person = person;
    }

    /* indo docs: Getters and setters */
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

    /* indo docs: toString
       Provides a concise string representation without causing recursion.
    */
    @Override
    public String toString() {
        String p = (person == null) ? "null" : "Person{id=" + person.getId() + ", name='" + person.getName() + "'}";
        return "AbandonedNet{id=" + id + ", location='" + location + "', size=" + size + ", status=" + status + ", person=" + p + "}";
    }
}
