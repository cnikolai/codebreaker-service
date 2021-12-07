package edu.cnm.deepdive.codebreaker.controller;

import edu.cnm.deepdive.codebreaker.model.entity.User;
import edu.cnm.deepdive.codebreaker.service.UserService;
import edu.cnm.deepdive.codebreaker.view.ScoreSummary;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController//spring: return value = response, method receives http request content and returns ?
@RequestMapping("/users")
public class UserController {

  private final UserService service;

  @Autowired //implicitly autowired, but can add here for clarity
  public UserController(UserService service) {
    this.service = service;
  }

  @GetMapping(value = "/me", produces = MediaType.APPLICATION_JSON_VALUE)
  public User me() {
    return service.getCurrentUser();
  }//Spring hands off to Jackson for serialization into JSON object

  @GetMapping(value = "/{externalKey}", produces = MediaType.APPLICATION_JSON_VALUE)
  public User get(@PathVariable UUID externalKey) { //or @PathVariable("id") = name of parameter, otherwise spelled exactly same as parameter, then ok.
    return service
        .getByExternalKey(externalKey)
        .orElseThrow(); //with an optional, returns content of optional if not empty, if empty throws a nosuchelement exception
  }

  @PutMapping(value = "/me", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public User put(@RequestBody User user) {
    return service.update(user, service.getCurrentUser());
  }

  @GetMapping(value = "/ranked", produces = MediaType.APPLICATION_JSON_VALUE,
      params = {"length", "poolSize", "order=count"})
  public Iterable<ScoreSummary> getRankingsByCount(@RequestParam int length, @RequestParam int poolSize) {
    return service.getRankingStatisticsByGuessCount(length, poolSize);
  }

  @GetMapping(value = "/ranked", produces = MediaType.APPLICATION_JSON_VALUE,
      params = {"length", "poolSize", "order=time"})
  public Iterable<ScoreSummary> getRankingsByTime(@RequestParam int length, @RequestParam int poolSize) {
    return service.getRankingStatisticsByTime(length, poolSize);
  }
}
