package br.com.jfr.libs.commons.api.context;

import static br.com.jfr.libs.commons.security.ContextHolder.CONTEXT_HOLDER_KEY;

import br.com.jfr.libs.commons.security.ContextHolder;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ContextFilter implements WebFilter {

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
    final ContextHolder contextHolder = new ContextHolder();
    exchange.getAttributes().put(CONTEXT_HOLDER_KEY, contextHolder);
    return chain.filter(exchange)
        .contextWrite(ctx -> ctx.put(CONTEXT_HOLDER_KEY, contextHolder));
  }

}
