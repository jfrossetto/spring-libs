package br.com.jfr.libs.commons.api.logging;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

@Component
@Order(100)
public class LoggingFilter implements WebFilter {
  private static final Logger LOGGER = LoggerFactory.getLogger(LoggingFilter.class);

  private static final String CALL_ID = "call_id";
  private static final String X_CALL_ID = "x-call-id";
  private static final String DURATION = "duration";
  private static final String HTTP_RESPONSE = "response";

  @Override
  public Mono<Void> filter(final ServerWebExchange exchange, final WebFilterChain chain) {
    final Instant start = Instant.now();

    return chain
        .filter(exchange)
        .contextWrite(
            ctx -> {
              LOGGER.info(
                  "Entering: [{}] {}",
                  exchange.getRequest().getMethod(),
                  exchange.getRequest().getURI());
              return fillContext(ctx, exchange);
            })
        .doAfterTerminate(() -> onTerminate(exchange, start));
  }

  private Context fillContext(Context ctx, ServerWebExchange exchange) {
    HttpHeaders headers = exchange.getRequest().getHeaders();
    final String callId = UUID.randomUUID().toString();
    Context context = ctx;

    /*
     * The keys that are added on the context, must be the same as in the file
     * resources/BluemoonLogstashLayoutV1.json
     */
    context = context.put(CALL_ID, callId);
    exchange.getResponse().getHeaders().add(X_CALL_ID, callId);
    return context;
  }

  private void onTerminate(ServerWebExchange exchange, Instant start) {
    final int statusCode =
        Optional.ofNullable(exchange.getResponse().getRawStatusCode()).orElse(-1);
    final Instant finish = Instant.now();
    MDC.put(DURATION, Long.toString(Duration.between(start, finish).toMillis()));
    MDC.put(HTTP_RESPONSE, Integer.toString(statusCode));
    LOGGER.info(
        "Exiting: [{}] {} with status code {}",
        exchange.getRequest().getMethod(),
        exchange.getRequest().getURI(),
        exchange.getResponse().getStatusCode());
    MDC.clear();
  }

}
