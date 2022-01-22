package br.com.jfr.libs.commons.exception;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Error {

  private String code;
  private String message;
  private String target;
  private final List<ErrorDetail> details = new ArrayList<>();

  public Error() {
    this("Internal Server Error");
  }

  public Error(String message) {
    this("InternalServerError", message);
  }

  public Error(String code, String message) {
    this.code = code;
    this.message = message;
  }

  public Error code(String code) {
    this.code = code;
    return this;
  }

  public Error message(String message) {
    this.message = message;
    return this;
  }

  public Error target(String target) {
    this.target = target;
    return this;
  }

  public Error addDetail(String code, String message) {
    details.add(new ErrorDetail(code, message));
    return this;
  }

  public Error addDetail(String code, String message, String target) {
    details.add(new ErrorDetail(code, message, target));
    return this;
  }

  public Error addDetails(List<ErrorDetail> details) {
    this.details.addAll(details);
    return this;
  }

  public String getCode() {
    return code;
  }

  public String getMessage() {
    return message;
  }

  public String getTarget() {
    return target;
  }

  public List<ErrorDetail> getDetails() {
    return Collections.unmodifiableList(details);
  }

}
