package de.iu.project.iuipwa0201ghostnetfishing.model;

import jakarta.persistence.*;
import java.io.Serializable;

/* indo docs: Person entity
   Represents a person who reports or recovers a ghost net. Stored in PERSON table.
*/
@Entity
@Table(name = "PERSON")
public class Person implements Serializable {

    /* indo docs: Primary key
       Auto-generated surrogate id for the Person entity.
    */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /* indo docs: Full name
       Name of the reporting/recovering person. Not null.
    */
    @Column(nullable = false)
    private String name;

    /* indo docs: Contact phone number
       Optional phone number; may be null for anonymous reports.
    */
    @Column(name = "PHONE_NUMBER")
    private String phoneNumber;

    /* indo docs: Protected no-args constructor
       Required by JPA to instantiate the entity via reflection.
    */
    protected Person() {
        // JPA
    }

    /* indo docs: All-args constructor
       Convenient constructor for tests and programmatic creation.
    */
    @SuppressWarnings("unused")
    public Person(Long id, String name, String phoneNumber) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    /* indo docs: Getters and setters */
    /**
     * Returns the ID.
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the ID of the object.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Returns the name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the object.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the phone number.
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Sets the phone number.
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /* indo docs: toString
       Concise representation excluding recursive relations.
    */
    @Override
    /**
     * Returns a string representation of the Person object.
     */
    public String toString() {
        return "Person{id=" + id + ", name='" + name + "'}";
    }
}
