package br.com.jfr.libs.commons.security;

public class ContextHolder {

  public static final String CONTEXT_HOLDER_KEY = "contextHolder";

  private Credential credential;

  public Credential getCredential() {
    return credential;
  }

  public void setCredential(Credential credential) {
    this.credential = credential;
  }

}
