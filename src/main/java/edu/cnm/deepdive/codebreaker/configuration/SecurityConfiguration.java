package edu.cnm.deepdive.codebreaker.configuration;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.jwt.JwtClaimValidator;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

@Configuration //makes this a spring component, configuration, component, controller, service, restcontroller = all spring components
@EnableWebSecurity //this entire app needs to use websecurity; can secure web endpoints
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

  private final Converter<Jwt, ? extends AbstractAuthenticationToken> converter;
  private String issuerUri;
  private String clientId;

  @Autowired //optional component, assumed automatically autowired
  public SecurityConfiguration(
      Converter<Jwt, ? extends AbstractAuthenticationToken> converter) {
    this.converter = converter;
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
        .authorizeRequests((auth) ->
            auth.anyRequest().anonymous()) //any request that comes in to any service is ok. credentials are not anonymous
        .oauth2ResourceServer()
        .jwt()
        .jwtAuthenticationConverter(converter);
  }

  //token encrypted by google, we don't know how to decrypt it.  we are going to tell the app where to go to decrypt it, see if token has expired or not
  //check to make sure token right for this service; validate: date, issuer, what webservice is it for, then ok
  public JwtDecoder jwtDecoder() {
    NimbusJwtDecoder decoder = JwtDecoders.fromIssuerLocation(issuerUri);
    OAuth2TokenValidator<Jwt> audienceValidator = new JwtClaimValidator<List<String>>(JwtClaimNames.AUD, (aud) -> aud.contains(clientId));
    OAuth2TokenValidator<Jwt> issuerValidator = JwtValidators.createDefaultWithIssuer(issuerUri);
    OAuth2TokenValidator<Jwt> combinedValidator = new DelegatingOAuth2TokenValidator<>(audienceValidator, issuerValidator);
    decoder.setJwtValidator(combinedValidator);
    return decoder;
  }
}
