package de.iu.project.iuipwa0201ghostnetfishing.BusinessLayer.Models;

/**
 * PersonBusinessLayerModel
 * ------------------------
 * Business-Layer-Repräsentation einer Person, die ein Geisternetz meldet oder
 * bei der Bergung hilft. Entspricht 1-zu-1 der Entity
 * {@link de.iu.project.iuipwa0201ghostnetfishing.DatabaseLayer.Models.PersonDataLayerModel},
 * enthält aber (wie üblich) keinerlei JPA-Annotationen.
 */
public class PersonBusinessLayerModel {

    // --- Attributes ---------------------------------------------------------

    /** Surrogat-Schlüssel der Person (wird in der Data-Layer-Entity generiert). */
    private Long id;

    /** Vollständiger Name (Pflichtfeld). */
    private String name;

    /** Optionale Telefonnummer für Rückfragen. */
    private String phoneNumber;

    // --- Constructors -------------------------------------------------------

    /** No-args-Konstruktor (wichtig für Mapper & Serialisierung). */
    public PersonBusinessLayerModel() { }

    /** All-args-Konstruktor – nützlich in Tests oder beim manuellen Erzeugen. */
    public PersonBusinessLayerModel(Long id, String name, String phoneNumber) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    // --- Simple Business Logic ---------------------------------------------

    /** @return {@code true} wenn keine Telefonnummer hinterlegt ist. */
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

    /** Zwei Business-Modelle gelten als gleich, wenn ihre IDs übereinstimmen. */
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