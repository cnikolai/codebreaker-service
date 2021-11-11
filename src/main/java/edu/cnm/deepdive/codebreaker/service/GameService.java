package edu.cnm.deepdive.codebreaker.service;

import edu.cnm.deepdive.codebreaker.model.dao.GameRepository;
import edu.cnm.deepdive.codebreaker.model.dao.GuessRepository;
import edu.cnm.deepdive.codebreaker.model.entity.Game;
import edu.cnm.deepdive.codebreaker.model.entity.Guess;
import edu.cnm.deepdive.codebreaker.model.entity.User;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service //Spring creates instances of class, not our code.
public class GameService {

  private final GameRepository gameRepository;
  private final GuessRepository guessRepository;
  private final Random rng;

  @Autowired //no spring component that is a random.
  public GameService(GameRepository repository,
      GuessRepository guessRepository, Random rng) {
    this.gameRepository = repository;
    this.guessRepository = guessRepository;
    this.rng = rng;
  }

  public Optional<Game> get(UUID id) {
    return gameRepository.findById(id);
  }

  public Optional<Game> get(UUID key, User user) {
    return gameRepository.findByExternalKeyAndUser(key, user);
  }

  public void delete(UUID id) {
    gameRepository.deleteById(id);
  }

  public void delete(UUID key, User user) {
    gameRepository
        .findByExternalKeyAndUser(key, user)
        .ifPresent(gameRepository::delete);
  }

  public Game startGame(String pool, int length, User user) {
    int[] codePoints = preprocess(pool);
    String code = generateCode(codePoints, length);
    Game game = new Game();
    game.setUser(user);
    game.setPool(new String(codePoints, 0, codePoints.length));
    game.setText(code);
    game.setLength(length);
    game.setPoolSize(
        codePoints.length);// not pool length because that gives us chars, and emojjis are 2 charrs
    return gameRepository.save(game);
  }

  public Guess processGuess(UUID gameKey, Guess guess, User user) throws IllegalArgumentException, IllegalStateException, NoSuchElementException {
    //if there is no game with this id belonging to this user
    return gameRepository
        .findByExternalKeyAndUser(gameKey, user)
        .map(
            (game) -> {// on an optional method, map method is skipped when there is no object returned by optional (i.e. a game)
              // int[] codeCodePoints = game.getPool().codePoints().toArray();//hashSet searches in constant time - uses a hash table, organizing contents such that can access them very quickly.
              //validate guess.
              //is the guess the wrong length?
              //does the guess have any characters that aren't in the pool?
              int[] guessCodePoints = preprocessGuess(guess, game);
              int[] codeCodePoints = getCodePoints(game.getText());
              computeMatches(guess, guessCodePoints, codeCodePoints);
              guess.setGame(game);
              return guessRepository.save(guess);
            })
        .orElseThrow(); //if optional object present, then returns it, otherwise throws an error
  }

  private void computeMatches(Guess guess, int[] guessCodePoints, int[] codeCodePoints) {
    int exactMatches = 0;
    //codepoint, count of occurrences of codepoint
    Map<Integer, Integer> guessCodePointCounts = new HashMap<>();
    Map<Integer, Integer> codeCodePointCounts = new HashMap<>();
    // Compute exact matches
    for (int i = 0; i < guessCodePoints.length; i++) {
      if (guessCodePoints[i] == codeCodePoints[i]) {
          exactMatches++;
      } else {
        guessCodePointCounts.put(guessCodePoints[i],
            1 + guessCodePointCounts.getOrDefault(guessCodePoints[i], 0));
        codeCodePointCounts.put(codeCodePoints[i],
            1 + codeCodePointCounts.getOrDefault(codeCodePoints[i], 0));
      }
    }
    guess.setExactMatches(exactMatches);
    // Compute near matches
    int nearMatches = guessCodePointCounts
        .entrySet()
        .stream()
        .mapToInt((entry) -> Math.min(entry.getValue(), codeCodePointCounts.getOrDefault(entry.getKey(),0)))
        .sum();
    guess.setNearMatches(nearMatches);
  }

  private int[] preprocessGuess(Guess guess, Game game) {
    if (game.isSolved()) {
      throw new IllegalStateException("Game is already solved.");
    }
    int[] guessCodePoints = getCodePoints(guess.getText());
    if (guessCodePoints.length != game.getLength()) {
      throw new IllegalArgumentException(String.format("Guess must have the same length (%d) as the secret code.", game.getLength()));
    }
    Set<Integer> poolCodePoints = game
        .getPool()
        .codePoints()
        .boxed()
        .collect(Collectors.toSet());
    if (IntStream.of(guessCodePoints)
        .anyMatch((codePoint) -> !poolCodePoints.contains(codePoint))) {
      throw new IllegalArgumentException(String.format("Guess may only contain the characters in the pool \"%s\"", game.getPool()));
    }
    return guessCodePoints;
  }

  //about failing, not stripping
  private int[] preprocess(String pool) {
    //turn string into stream of unicode codepoints
    return pool
        .codePoints()
        .peek((codePoint) -> {
          if (!Character.isDefined(codePoint)) {
            throw new IllegalArgumentException(
                String.format("Undefined character in pool: %d", codePoint));
          } else if (Character.isWhitespace(codePoint)) {
            throw new IllegalArgumentException(
                String.format("Whitespace character in pool: %d", codePoint));
          }
        })
        .sorted() //ascending numerical order
        .distinct()
        .toArray();
    //.takeWhile((codePoint) -> Character.isDefined(codePoint) &&
    //  !Character.isWhitespace(codePoint))
  }

  private String generateCode(int[] codePoints, int length) {
    //secure random performance not great
    int[] code = IntStream.generate(() -> codePoints[rng.nextInt(codePoints.length)])
        .limit(length)
        .toArray();
    return new String(code, 0, code.length);
  }

  private int[] getCodePoints(String source) {
    return source
        .codePoints()
        .toArray();
  }
}
