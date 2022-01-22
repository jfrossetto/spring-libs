package br.com.jfr.libs.commons.security;

import java.util.UUID;

public class Credential {

  private UUID userId;
  private String authToken;

  public Credential() {
  }

  public Credential userId(UUID userId) {
    this.userId = userId;
    return this;
  }

  public Credential authToken(String authToken) {
    this.authToken = authToken;
    return this;
  }

  public UUID getUserId() {
    return userId;
  }

  public String getAuthToken() {
    return authToken;
  }
}
