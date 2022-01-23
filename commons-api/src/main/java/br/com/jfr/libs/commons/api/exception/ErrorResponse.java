package br.com.jfr.libs.commons.api.exception;

import br.com.jfr.libs.commons.exception.Error;

public class ErrorResponse {

  private final Error error;

  public ErrorResponse(Error error) {
    this.error = error;
  }

  public Error getError() {
    return error;
  }
}
