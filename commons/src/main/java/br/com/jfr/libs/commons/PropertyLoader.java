package br.com.jfr.libs.commons;

import java.util.Arrays;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;

public class PropertyLoader {

  private static final Logger LOGGER = LoggerFactory.getLogger(PropertyLoader.class);

  public static void load(Environment env) {
    LOGGER.info("loading properties ....");
    MutablePropertySources propSrcs = ((AbstractEnvironment) env).getPropertySources();
    StreamSupport.stream(propSrcs.spliterator(), false)
        .filter(ps -> ps instanceof EnumerablePropertySource)
        .map(ps -> ((EnumerablePropertySource<?>) ps).getPropertyNames())
        .flatMap(Arrays::stream)
        .distinct()
        .forEach(key -> System.setProperty(key, env.getProperty(key)));
  }

}
