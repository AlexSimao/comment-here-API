package com.alex.projectComment.infra.security;

import com.alex.projectComment.entities.User;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

  @Value("${spring.application.name}")
  private String projectName;

  @Value("${api.security.token.secret}")
  private String secret;

  @Value("${api.security.token.expiration-minutes}") // Tempo de expiração (2 horas padrão)
  private long expirationMinutes;

  private Instant generateExpirationDate() {
    return LocalDateTime.now().plusMinutes(expirationMinutes).toInstant(ZoneOffset.of("-03:00"));
  }

  public String generateKeyToken(User user) {
    try {
      Algorithm algorithm = Algorithm.HMAC256(secret);

      String token = JWT.create().withIssuer(projectName)
          .withSubject(user.getUsername())
          .withClaim("email", user.getEmail())
          .withClaim("id", user.getId())
          .withExpiresAt(generateExpirationDate())
          .sign(algorithm);
      return token;

    } catch (JWTCreationException exception) {
      throw new RuntimeException("Erro na geração de Token" + exception);
    }
  }

  public String validateToken(String token) {
    try {
      Algorithm algorithm = Algorithm.HMAC256(secret);

      return JWT.require(algorithm)
          .withIssuer(projectName)
          .build()
          .verify(token)
          .getSubject();

    } catch (JWTVerificationException exception) {
      // Token inválido será tratado como (403-Forbidden) no SecurityFilter
      return null;
    }
  }

  public Long getTokenId(String token) {
    try {
      Algorithm algorithm = Algorithm.HMAC256(secret);

      return JWT.require(algorithm)
          .withIssuer(projectName)
          .build()
          .verify(token)
          .getClaim("id").asLong();

    } catch (JWTVerificationException exception) {
      // Token inválido será tratado como (403-Forbidden) no SecurityFilter
      return null;
    }
  }

  public String recoverToken(HttpServletRequest request) {
    var authHeader = request.getHeader("Authorization");
    if (authHeader == null)
      return null;
    return authHeader.replace("Bearer ", "");
  }
}