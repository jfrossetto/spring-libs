package br.com.jfr.libs.commons.api.security;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import br.com.jfr.libs.commons.security.ContextHolder;
import br.com.jfr.libs.commons.security.Credential;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@Order(1000)
public class SecurityFilter implements WebFilter {

  private static final Logger LOGGER = LoggerFactory.getLogger(SecurityFilter.class);

  private final RequestMappingHandlerMapping handlerMapping;

  public SecurityFilter(RequestMappingHandlerMapping handlerMapping) {
    this.handlerMapping = handlerMapping;
  }

  @Override
  @Secured
  public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
    return handlerMapping
        .getHandlerInternal(exchange)
        .map(securedHandler ->
              Optional.ofNullable(securedHandler.getMethodAnnotation(Secured.class))
                  .orElseGet(
                      () -> {
                        try {
                          return SecurityFilter.class
                              .getMethod("filter", ServerWebExchange.class, WebFilterChain.class)
                              .getAnnotation(Secured.class);
                        } catch (final NoSuchMethodException | SecurityException e) {
                          throw new RuntimeException(e.getMessage(), e);
                        }
                      }))
        .filter(annotation -> !annotation.unprotected())
        .map( annotation -> {
          final String authorizationHeader =
              Optional.ofNullable(exchange.getRequest().getHeaders().getFirst(AUTHORIZATION))
                  .orElseThrow(() -> new UnauthorizedException("Authorization required"));
          return authorizationHeader;
        })
        .doOnNext(token -> LOGGER.info("SecurityFilter {} ", token))
        .flatMap( token -> {
          final ContextHolder contextHolder =exchange.<ContextHolder>getAttribute(ContextHolder.CONTEXT_HOLDER_KEY);
          Mono<Credential> credentialMono = validateToken(token);
          return credentialMono
              .doOnNext(credential -> LOGGER.info("Cred {}", credential.getUserId()))
              .doOnNext(contextHolder::setCredential)
              .then();
        })
        .then(chain.filter(exchange));
  }

  private Mono<Credential> validateToken(String token) {
    return Mono.just(token)
        .map( t -> getCredFromToken(t) );
  }

  private Credential getCredFromToken(String token) {
    if(ObjectUtils.isEmpty(token) || !token.equals("token-api")) {
      throw new UnauthorizedException("Invalid Token");
    }
    return new Credential()
        .userId(UUID.fromString("00000000-0000-0000-0000-000000000000"))
        .authToken(token);
  }
}
