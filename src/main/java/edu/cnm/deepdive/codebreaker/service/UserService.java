package edu.cnm.deepdive.codebreaker.service;

import edu.cnm.deepdive.codebreaker.model.dao.UserRepository;
import edu.cnm.deepdive.codebreaker.model.entity.User;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service //spring service class
public class UserService implements Converter<Jwt, UsernamePasswordAuthenticationToken> {

  private final UserRepository repository;

  @Autowired
  public UserService(UserRepository repository) {
    this.repository = repository;
  }

  @Override //every request presents a bearer token and that gets validated on every request.
  public UsernamePasswordAuthenticationToken convert(
      Jwt source) {//jwt source totally validated as input here
    Collection<SimpleGrantedAuthority> grants =
        Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));
    return new UsernamePasswordAuthenticationToken(
        getOrCreate(source.getSubject(), source.getClaimAsString("name")),
        source.getTokenValue(),
        grants);//principle(user object), value of jwt, authorities that granted
  }

  //looks up a user by OAuth key and if it doesn't exist, creates one
  public User getOrCreate(String oauthKey, String displayName) {
    return repository
        .findByOauthKey(
            oauthKey) //either contains a User or nothing at all because of Optional return type
        .orElseGet(() -> {
          User user = new User();
          user.setOauthKey(oauthKey);
          user.setDisplayName(displayName);
          return repository.save(user);
        });
  }

  public Optional<User> get(UUID id) {
    return repository.findById(id);
  }

  public Optional<User> getByExternalKey(UUID key) {
    return repository.findByExternalKey(key);
  }

  public Iterable<User> getAll() {
    return repository.getAllByOrderByDisplayNameAsc(); //return repository.findAll(Sort.by("displayName"))
  }

  public User save(User user) {
    return repository.save(user);
  }

  public void delete(User user) {
    repository.delete(user);
  }

  /**
   * Returns the {@link User} associated with the authenticated
   *
   * @return
   */
  public User getCurrentUser() {
    return (User) SecurityContextHolder
        .getContext() //gets context of thread on
        .getAuthentication()
        .getPrincipal();
  }

  /**
   * Updates the current user record from the provided updated user record, and saves the result to
   * the database.
   *
   * @param updatedUser User deserialized from body of request
   * @param user Current requestor
   * @return Updated user instance
   */
  public User update(User updatedUser, User user) {
    if (updatedUser.getDisplayName() != null) {
      user.setDisplayName(updatedUser.getDisplayName());
    }
    return save(user);
  }
}
