package edu.cnm.deepdive.codebreaker.service;

import edu.cnm.deepdive.codebreaker.model.dao.UserRepository;
import edu.cnm.deepdive.codebreaker.model.entity.User;
import java.util.Collection;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service //spring service class
public class UserService implements Converter<Jwt, UsernamePasswordAuthenticationToken> {

  private final UserRepository repository;

  @Autowired
  public UserService(UserRepository repository) {
    this.repository = repository;
  }

  @Override
  public UsernamePasswordAuthenticationToken convert(Jwt source) {//jwt source totally validated as input here
    Collection<SimpleGrantedAuthority> grants =
        Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));
    return new UsernamePasswordAuthenticationToken(
        getOrCreate(source.getSubject(), source.getClaimAsString("name")),
        source.getTokenValue(), grants);//principle(user object), value of jwt, authorities that granted
  }

  //looks up a user by OAuth key and if it doesn't exist, creates one
  public User getOrCreate(String oauthKey, String displayName) {
    return repository
        .findByOauthKey(oauthKey) //either contains a User or nothing at all because of Optional return type
        .orElseGet(() -> {
          User user = new User();
          user.setOauthKey(oauthKey);
          user.setDisplayName(displayName);
          return repository.save(user);
        });
  }
}
