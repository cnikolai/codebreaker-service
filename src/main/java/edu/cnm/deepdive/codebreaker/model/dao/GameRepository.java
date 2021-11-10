package edu.cnm.deepdive.codebreaker.model.dao;

import edu.cnm.deepdive.codebreaker.model.entity.Game;
import edu.cnm.deepdive.codebreaker.model.entity.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

//define operations beyond simple CRUD
//<Game, UUID> = type of entity and type of primary key
public interface GameRepository extends JpaRepository<Game, UUID> {

  Optional<Game> findByExternalKey(UUID key);

  Optional<Game> findByExternalKeyAndUser(UUID key, User user);
}
