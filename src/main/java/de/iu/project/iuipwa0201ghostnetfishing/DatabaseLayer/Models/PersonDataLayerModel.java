package de.iu.project.iuipwa0201ghostnetfishing.DatabaseLayer.Models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.io.Serializable;

/* Person entity
   Represents a person who reports or recovers a ghost net. Stored in PERSON table.
*/
@Entity
@Table(name = "PERSON")
public class PersonDataLayerModel implements Serializable {

    /* Primary key
       Auto-generated surrogate id for the Person entity.
    */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /* Full name
       Name of the reporting/recovering person. Not null.
    */
    @NotBlank
    @Size(max = 120)
    @Column(nullable = false, length = 120)
    private String name;

    /* Contact phone number
       Optional phone number; may be null for anonymous reports.
    */
    @Size(max = 40)
    @Column(name = "PHONE_NUMBER", length = 40)
    private String phoneNumber;

    /* Protected no-args constructor
       Required by JPA to instantiate the entity via reflection.
    */
    public PersonDataLayerModel() {
        // JPA
    }

    /* All-args constructor
       Convenient constructor for tests and programmatic creation.
    */
    @SuppressWarnings("unused")
    public PersonDataLayerModel(Long id, String name, String phoneNumber) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    /* Getters and setters */
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /* toString
       Concise representation excluding recursive relations.
    */
    @Override
    public String toString() {
        return "Person{id=" + id + ", name='" + name + "'}";
    }
}
