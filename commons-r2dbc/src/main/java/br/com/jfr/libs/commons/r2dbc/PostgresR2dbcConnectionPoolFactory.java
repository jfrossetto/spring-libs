package br.com.jfr.libs.commons.r2dbc;

import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.pool.ConnectionPoolConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import io.r2dbc.spi.ConnectionFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PostgresR2dbcConnectionPoolFactory {
  private static final Logger LOGGER = LoggerFactory.getLogger(PostgresR2dbcConnectionPoolFactory.class);

  public ConnectionFactory create() {

    final String username = System.getProperty("datasource.user");
    final String password = System.getProperty("datasource.password");
    final String host = System.getProperty("datasource.serverName", "localhost");
    final String port = System.getProperty("datasource.portNumber", "5432");
    final String database = System.getProperty("datasource.databaseName", "dbdev");
    final String connectionTimeout = System.getProperty("datasource.connectionTimeout", "15000");

    String applicationName = System.getProperty("instanceName");
    if (applicationName == null || applicationName.isBlank()) {
      try {
        applicationName = InetAddress.getLocalHost().getHostName();
      } catch (final UnknownHostException e) {
        applicationName = "unknownJavaApplication";
      }
    }

    // Pool
    final String initialSize = System.getProperty("datasource.minimumIdle", "2");
    final String maxSize = System.getProperty("datasource.maximumPoolSize", "3");
    final String idleTimeout = System.getProperty("datasource.idleTimeout", "330000");
    final String maxLifetime = System.getProperty("datasource.maxLifetime", "900000");

    final Map<String, String> options = new HashMap<>();
    options.put("lock_timeout", "10s");

    // Creates a ConnectionPool wrapping an underlying ConnectionFactory
    final ConnectionFactory connectionFactory =
        new PostgresqlConnectionFactory(
            PostgresqlConnectionConfiguration.builder()
                .host(host)
                .port(Integer.parseInt(port))
                .username(username)
                .password(password)
                .database(database)
                .connectTimeout(Duration.ofMillis(Long.parseLong(connectionTimeout)))
                .fetchSize(1000)
                .preparedStatementCacheQueries(-1)
                .schema("public")
                .tcpKeepAlive(false)
                .tcpNoDelay(true)
                .options(options)
                .applicationName(applicationName)
                .build());

    return new ConnectionPool(
        ConnectionPoolConfiguration.builder(connectionFactory)
            .maxIdleTime(Duration.ofMillis(Long.parseLong(idleTimeout)))
            .initialSize(Integer.parseInt(initialSize))
            .maxSize(Integer.parseInt(maxSize))
            .acquireRetry(3)
            .maxLifeTime(Duration.ofMillis(Long.parseLong(maxLifetime)))
            .validationQuery("SELECT 1")
            .build());
  }

}
