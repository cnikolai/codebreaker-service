package edu.cnm.deepdive.codebreaker.model.dao;

import edu.cnm.deepdive.codebreaker.model.entity.User;
import edu.cnm.deepdive.codebreaker.view.ScoreSummary;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, UUID> { //Spring Data knows how to insert, delete, and update data in the database

  String RANKING_STATISTICS_QUERY = "SELECT "
      + "  u.user_id AS userId, "
      + "  u.display_name AS displayName, "
      + "  u.external_key AS externalKey, "
      + "  gs.averageGuessCount, "
      + "  gs.averageTime "
      + "FROM user_profile AS u "
      + "INNER JOIN ("
      + "  SELECT "
      + "    g.user_id, "
      + "    AVG(gu.total_guesses) AS averageGuessCount, "
      + "    AVG(DATEDIFF(MILLISECOND, gu.first_guess, gu.last_guess)) AS averageTime "
      + "  FROM game AS g "
      + "  INNER JOIN ("
      + "    SELECT "
      + "      game_id, "
      + "      COUNT(*) as total_guesses, "
      + "      MIN(created) AS first_guess, "
      + "      MAX(created) AS last_guess, "
      + "      MAX(exact_matches) AS match_count "
      + "    FROM guess "
      + "    GROUP BY game_id"
      + "  ) AS gu ON gu.game_id = g.game_id "
      + "  WHERE "
      + "    g.length = :length "
      + "    AND g.pool_size = :poolSize "
      + "    AND gu.match_count = g.length "
      + "  GROUP BY g.user_id"
      + ") AS gs ON gs.user_id = u.user_id ";

  Optional<User> findByOauthKey(String oauthKey); //showing us things in entity class

  Optional<User> findByExternalKey(UUID externalKey); //showing us things in entity class, Optional => don't have to define if xx != null all the time

  // or could do @Query("SELECT User FROM User AS u ORDER BY u.displayName ") //Note: jpql (not sql) = idealized sql that works with jpa
  //@Query("SELECT * FROM user_profile AS u ORDER BY u.displayName ", nativeQuery = true) if want to use sql
  Iterable<User> getAllByOrderByDisplayNameAsc(); //camel casing is what spring data uses to split this apart to figure out what this has to be.

  @Query(value = RANKING_STATISTICS_QUERY + "ORDER BY averageGuessCount ASC, averageTime ASC",
      nativeQuery = true)
  Iterable<ScoreSummary> getScoreSummariesOrderByGuessCount(int length, int poolSize);

  @Query(value = RANKING_STATISTICS_QUERY + "ORDER BY averageTime ASC, averageGuessCount ASC",
      nativeQuery = true)
  Iterable<ScoreSummary> getScoreSummariesOrderByTime(int length, int poolSize);

//  @Query(value = "SELECT u.user_id, u.display_name as displayName, u.external_key as externalKey, gs.averageGuessCount, gs.averageTime FROM "
//      + "user_profile AS u "
//      + "INNER JOIN ("
//      + "  SELECT g.user_id, AVG(total_guesses) AS averageGuessCount, AVG(DATEDIFF(MILLISECOND, gu.last_guess, gu.first_guess)) AS averageTime "
//      + "  FROM game AS g "
//      + "  INNER JOIN ("
//      + "    SELECT "
//      + "      game_id, "
//      + "      COUNT(*) AS total_guesses, "
//      + "      MIN(created) AS st_guess, "
//      + "      MAX(created) AS last_guess, "
//      + "      MAX(exact_matches) AS match_count "
//      + "    FROM guess "
//      + "    GROUP BY game_id"
//      + "  ) AS gu ON gu.game_id = g.game_id "
//      + "  WHERE "
//      + "    g.length = :length AND g.pool_size = :poolSize "
//      + "    AND gu.match_count = g.length"
//      + "   GROUP BY g.user_id"
//      + ") AS gs ON gs.user_id = u.user_id "
//      , nativeQuery = true) //jpl query before turned into SQL query
//  Iterable<ScoreSummary> getScoreSummaries(int length, int poolSize);
//
//  interface ScoreSummary {
//
//    String getDisplayName();
//
//    UUID getExternalKey();
//
//    double getAverageGuessCount();
//
//    long getAverageTime();
//
//  }
}
