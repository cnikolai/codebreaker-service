package edu.cnm.deepdive.codebreaker.model.dao;

import edu.cnm.deepdive.codebreaker.model.entity.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, UUID> { //Spring Data knows how to insert, delete, and update data in the database

  Optional<User> findByOauthKey(String oauthKey); //showing us things in entity class

  Optional<User> findByExternalKey(UUID externalKey); //showing us things in entity class, Optional => don't have to define if xx != null all the time

}
