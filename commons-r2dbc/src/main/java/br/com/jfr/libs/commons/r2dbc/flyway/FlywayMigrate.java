package br.com.jfr.libs.commons.r2dbc.flyway;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FlywayMigrate {

  private static final Logger LOGGER = LoggerFactory.getLogger(FlywayMigrate.class);

  public static void migrate() {

    var flywayEnabled = Boolean.parseBoolean(System.getProperty("flyway.enabled", "false"));
    if (!flywayEnabled) {
      LOGGER.info("Flyway is disabled, skipping migration.");
      return;
    }
    LOGGER.info("Flyway is enabled, start migration.");

    var host = System.getProperty("datasource.serverName", "localhost");
    var port = Integer.parseInt(System.getProperty("datasource.portNumber", "5432"));
    var database = System.getProperty("datasource.databaseName", "dbdev");

    var flywayUser = System.getProperty("flyway.user", "flyway");
    var flywayPassword = System.getProperty("flyway.password", "flyway");

    var schemas = System.getProperty("flyway.schemas", "public");
    var baselineOnMigrate =
        Boolean.parseBoolean(System.getProperty("flyway.enableBaselineOnMigration", "false"));
    var baselineVersion = System.getProperty("flyway.baselineVersion", "0");
    var outOfOrder = Boolean.parseBoolean(
        System.getProperty("flyway.outOfOrder", "true"));

    var flyway =
        new Flyway(
            new FluentConfiguration()
                .defaultSchema("public")
                .dataSource(
                    String.format("jdbc:postgresql://%s:%d/%s", host, port, database),
                    flywayUser,
                    flywayPassword)
                .cleanDisabled(true)
                .schemas(schemas)
                .baselineOnMigrate(baselineOnMigrate)
                .baselineVersion(baselineVersion)
                .outOfOrder(outOfOrder));

    flyway.repair();
    flyway.migrate();
  }

}
