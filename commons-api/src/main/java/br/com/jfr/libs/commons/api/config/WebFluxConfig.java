package br.com.jfr.libs.commons.api.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.ReactivePageableHandlerMethodArgumentResolver;
import org.springframework.data.web.ReactiveSortHandlerMethodArgumentResolver;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer;

@Configuration
public class WebFluxConfig implements WebFluxConfigurer {

  private ObjectMapper mapper;

  public WebFluxConfig(ObjectMapper mapper) {
    this.mapper = mapper;
  }

  @Override
  public void configureHttpMessageCodecs(ServerCodecConfigurer configurer) {
    configurer.defaultCodecs().jackson2JsonEncoder(new Jackson2JsonEncoder(mapper));
    configurer.defaultCodecs().jackson2JsonDecoder(new Jackson2JsonDecoder(mapper));
  }

  @Override
  public void configureArgumentResolvers(ArgumentResolverConfigurer configurer) {
    final ReactivePageableHandlerMethodArgumentResolver pageableResolver =
        new ReactivePageableHandlerMethodArgumentResolver();
    // setting defaults
    pageableResolver.setFallbackPageable(PageRequest.of(0, 50));

    configurer.addCustomResolver(pageableResolver, new ReactiveSortHandlerMethodArgumentResolver());
  }
}
