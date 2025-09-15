package de.iu.project.iuipwa0201ghostnetfishing.repository;

import de.iu.project.iuipwa0201ghostnetfishing.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/* info docs: PersonRepository interface
   Spring Data JPA repository for Person entity CRUD operations.
   Extends JpaRepository to provide common persistence methods.
*/
@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {

    /* info docs: find by phone number
       Optional helper to find a person by their phoneNumber property.
    */
    Optional<Person> findByPhoneNumber(String phoneNumber);
}
