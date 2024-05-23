package dev.lydtech.component.framework.client.database;

import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Optional;

import static dev.lydtech.component.framework.configuration.TestcontainersConfiguration.MARIADB_DATABASE_NAME;
import static dev.lydtech.component.framework.configuration.TestcontainersConfiguration.MARIADB_PASSWORD;
import static dev.lydtech.component.framework.configuration.TestcontainersConfiguration.MARIADB_SCHEMA_NAME;
import static dev.lydtech.component.framework.configuration.TestcontainersConfiguration.MARIADB_USERNAME;

@Slf4j
public class MariaDbClient {

        private static dev.lydtech.component.framework.client.database.MariaDbClient instance;
        private static String dbHostAndPortUrl;

        private MariaDbClient(){
            String mariaDbHost = Optional.ofNullable(System.getProperty("docker.host"))
                    .orElse("localhost");
            String mariaDbPort = Optional.ofNullable(System.getProperty("mariadb.mapped.port"))
                    .orElseThrow(() -> new RuntimeException("mariadb.mapped.port property not found"));
            dbHostAndPortUrl = "jdbc:mariadb://" + mariaDbHost + ":" + mariaDbPort + "/";
        }

        public synchronized static MariaDbClient getInstance() {
            if(instance==null) {
                instance = new dev.lydtech.component.framework.client.database.MariaDbClient();
            }
            return instance;
        }

        /**
         * Connect to MariaDB with the database properties as set in System properties.
         */
        public static Connection getConnection() throws Exception {
            return getConnection(MARIADB_DATABASE_NAME, MARIADB_SCHEMA_NAME, MARIADB_USERNAME, MARIADB_PASSWORD);
        }

        /**
         * Connect to MariaDB with the database properties passed in.
         *
         * This is useful when re-running component tests with the containers left up between runs and not wanting to set
         * the system properties for each test run.
         */
        public static Connection getConnection(String databaseName, String schemaName, String username, String password) throws Exception {
            String dbUrl = dbHostAndPortUrl + databaseName + "?currentSchema=" + schemaName;
            Connection connection = DriverManager.getConnection(dbUrl, username, password);
            log.debug("Connected to MariaDB at: "+dbUrl);
            return connection;
        }

        /**
         * Close the MariaDB connection.
         */
        public static void close(Connection connection) throws Exception {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        }

        /**
         * Get the URL of the database with the host and port.  The port is the mapped port of the docker container, such that
         * this can be used to connect to from the tests.
         */
        public static String getDbHostAndPortUrl() {
            return dbHostAndPortUrl;
        }

}
