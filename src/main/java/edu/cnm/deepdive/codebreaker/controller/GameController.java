package edu.cnm.deepdive.codebreaker.controller;

import edu.cnm.deepdive.codebreaker.model.entity.Game;
import edu.cnm.deepdive.codebreaker.service.GameService;
import edu.cnm.deepdive.codebreaker.service.UserService;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/games")
public class GameController {

  private final UserService userService;
  private final GameService gameService;

  @Autowired
  public GameController(UserService userService,
      GameService gameService) {
    this.userService = userService;
    this.gameService = gameService;
  }

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public Game post(@RequestBody Game game) {
    return gameService.startGame(game, userService.getCurrentUser());
  }

  @GetMapping(value = "/{externalKey}", produces = MediaType.APPLICATION_JSON_VALUE)
  public Game get(@PathVariable UUID externalKey) {
    return gameService
        .get(externalKey, userService.getCurrentUser())
        .orElseThrow();
  }

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  //@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE, method = "GET")
  public Iterable<Game> get() {
    return userService
        .getCurrentUser()
        .getGames(); //serialized a list of games for current user
  }

  @DeleteMapping(value = "/{externalKey}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  //@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE, method = "GET")
  public void delete(@PathVariable UUID externalKey) {//could also do@PathVariable("key") and name it key in url
    gameService.delete(externalKey, userService.getCurrentUser());
  }
}
