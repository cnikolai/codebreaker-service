package edu.cnm.deepdive.codebreaker.model.dao;

import edu.cnm.deepdive.codebreaker.model.entity.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, UUID> { //Spring Data knows how to insert, delete, and update data in the database

  Optional<User> findByOauthKey(String oauthKey); //showing us things in entity class

  Optional<User> findByExternalKey(UUID externalKey); //showing us things in entity class, Optional => don't have to define if xx != null all the time

  // or could do @Query("SELECT User FROM User AS u ORDER BY u.displayName ") //Note: jpql (not sql) = idealized sql that works with jpa
  //@Query("SELECT * FROM user_profile AS u ORDER BY u.displayName ", nativeQuery = true) if want to use sql
  Iterable<User> getAllByOrderByDisplayNameAsc(); //camel casing is what spring data uses to split this apart to figure out what this has to be.


}
