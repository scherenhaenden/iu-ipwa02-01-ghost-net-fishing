package de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Models;

/**
 * PersonBusinessLayerModel
 * ------------------------
 * Business-layer representation of a person who reports or recovers a ghost net.
 * Maps 1:1 to the entity
 * {@link de.iu.project.iuipwa0201ghostnetfishing.DatabaseLayer.Models.PersonDataLayerModel},
 * but (as usual) contains no JPA annotations.
 */
public class PersonBusinessLayerModel {

    // --- Attributes ---------------------------------------------------------

    /** Surrogate key of the person (generated in the data-layer entity). */
    private Long id;

    /** Full name (required). */
    private String name;

    /** Optional phone number for follow-up. */
    private String phoneNumber;

    // --- Constructors -------------------------------------------------------

    /** No-args constructor (important for mappers & serialization). */
    public PersonBusinessLayerModel() { }

    /** All-args constructor – useful in tests or when creating instances manually. */
    public PersonBusinessLayerModel(Long id, String name, String phoneNumber) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    // --- Simple Business Logic ---------------------------------------------

    /** @return {@code true} when no phone number is present. */
    public boolean isAnonymous() {
        return phoneNumber == null || phoneNumber.isBlank();
    }

    // --- Getters & Setters --------------------------------------------------

    public Long getId()         { return id; }
    public void setId(Long id)  { this.id = id; }

    public String getName()           { return name; }
    public void setName(String name)  { this.name = name; }

    public String getPhoneNumber()                 { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    // --- Object overrides ---------------------------------------------------

    @Override
    public String toString() {
        return "PersonBusinessLayerModel{id=" + id + ", name='" + name + "'}";
    }

    /** Two business models are considered equal when their IDs match. */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PersonBusinessLayerModel other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}