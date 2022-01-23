package br.com.jfr.libs.commons.api.security;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import br.com.jfr.libs.commons.api.exception.WebException;
import br.com.jfr.libs.commons.exception.Error;

public class UnauthorizedException extends WebException {

  private static final long serialVersionUID = -7230427078682415322L;

  public UnauthorizedException(final String message) {
    super(UNAUTHORIZED, new Error().message(message).code(UNAUTHORIZED.toString()));
  }

  public UnauthorizedException(final String message, final Throwable cause) {
    super(UNAUTHORIZED, new Error().message(message).code(UNAUTHORIZED.toString()), cause);
  }
}

