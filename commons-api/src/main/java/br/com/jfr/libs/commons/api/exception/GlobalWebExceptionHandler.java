package br.com.jfr.libs.commons.api.exception;

import br.com.jfr.libs.commons.exception.BusinessException;
import br.com.jfr.libs.commons.exception.Error;
import br.com.jfr.libs.commons.exception.ErrorDetail;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalWebExceptionHandler implements ErrorWebExceptionHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(GlobalWebExceptionHandler.class);

  private final ObjectMapper objectMapper;

  public GlobalWebExceptionHandler(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  public Mono<Void> handle(ServerWebExchange exchange, Throwable exception) {
    Objects.requireNonNull(exchange);
    LOGGER.error(exception.getMessage(), exception);
    if (exception instanceof WebException) {
      WebException webException = (WebException) exception;
      return generateResponse(exchange, webException.getError(), webException.getStatus());
    }

    if (exception instanceof OptimisticLockingFailureException
        || exception.getCause() instanceof OptimisticLockingFailureException) {
      var error =
          new Error()
              .code(HttpStatus.CONFLICT.getReasonPhrase())
              .message("Failed to update entity. Version does not match.")
              .target("RequestBody");
      return generateResponse(exchange, error, HttpStatus.CONFLICT);
    }

    if (exception instanceof BusinessException) {
      BusinessException businessException = (BusinessException) exception;
      return generateResponse(exchange, businessException.getError(), HttpStatus.BAD_REQUEST);
    }

    if (exception instanceof WebExchangeBindException) {
      WebExchangeBindException validationException = (WebExchangeBindException) exception;
      List<ErrorDetail> details =
          validationException.getFieldErrors().stream()
              .map(
                  error ->
                      new ErrorDetail(error.getCode(), error.getDefaultMessage(), error.getField()))
              .collect(Collectors.toList());

      Error error =
          new Error()
              .code("BadArgument")
              .message("Invalid request body")
              .target("RequestBody")
              .addDetails(details);
      return generateResponse(exchange, error, HttpStatus.BAD_REQUEST);
    }

    if (!exchange.getResponse().getStatusCode().is4xxClientError()
        && !exchange.getResponse().getStatusCode().is5xxServerError()) {
      exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    Error error = new Error().code("UnknownError").message("Internal Server Error.");
    return generateResponse(exchange, error, exchange.getResponse().getStatusCode());
  }

  private Mono<Void> generateResponse(ServerWebExchange exchange, Error error, HttpStatus status) {
    HttpHeaders headers = exchange.getResponse().getHeaders();

    DataBufferFactory bufferFactory = exchange.getResponse().bufferFactory();
    headers.setContentType(MediaType.APPLICATION_JSON);
    exchange.getResponse().setStatusCode(status);
    DataBuffer dataBuffer = bufferFactory.wrap(toBytesResponse(error));
    return exchange.getResponse().writeWith(Mono.just(dataBuffer));
  }

  private byte[] toBytesResponse(Error error) {
    try {
      return Optional.ofNullable(objectMapper.writeValueAsString(new ErrorResponse(error)))
          .map(String::getBytes)
          .orElse("failed to convert error to json".getBytes(StandardCharsets.UTF_8));
    } catch (Exception e) {
      LOGGER.debug("fail to convert Error to Json", e);
      return "failed to convert error to json".getBytes(StandardCharsets.UTF_8);
    }
  }
}
