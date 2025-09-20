package de.iu.project.iuipwa0201ghostnetfishing.DatabaseLayer.Repositories;

import de.iu.project.iuipwa0201ghostnetfishing.DatabaseLayer.Models.PersonDataLayerModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/* info docs: PersonRepository interface
   Spring Data JPA repository for Person entity CRUD operations.
   Extends JpaRepository to provide common persistence methods.
*/
@Repository
public interface PersonDataLayerModelRepository extends JpaRepository<PersonDataLayerModel, Long> {

    /* info docs: find by phone number
       Optional helper to find a person by their phoneNumber property.
    */
    Optional<PersonDataLayerModel> findByPhoneNumber(String phoneNumber);

    /* Derived query: find persons whose name contains the given text (case-insensitive)
       Useful for simple search/autocomplete features.
    */
    List<PersonDataLayerModel> findByNameContainingIgnoreCase(String namePart);
}
