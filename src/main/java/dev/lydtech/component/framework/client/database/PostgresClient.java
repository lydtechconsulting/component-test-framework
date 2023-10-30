package dev.lydtech.component.framework.client.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;

import static dev.lydtech.component.framework.extension.TestContainersConfiguration.POSTGRES_DATABASE_NAME;
import static dev.lydtech.component.framework.extension.TestContainersConfiguration.POSTGRES_PASSWORD;
import static dev.lydtech.component.framework.extension.TestContainersConfiguration.POSTGRES_SCHEMA_NAME;
import static dev.lydtech.component.framework.extension.TestContainersConfiguration.POSTGRES_USERNAME;

@Slf4j
public class PostgresClient {

    private static PostgresClient instance;
    private static String dbHostAndPortUrl;

    private PostgresClient(){
        String postgresHost = Optional.ofNullable(System.getProperty("docker.host"))
                .orElse("localhost");
        String postgresPort = Optional.ofNullable(System.getProperty("postgres.mapped.port"))
                .orElseThrow(() -> new RuntimeException("postgres.mapped.port property not found"));
        dbHostAndPortUrl = "jdbc:postgresql://" + postgresHost + ":" + postgresPort + "/";
    }

    public synchronized static PostgresClient getInstance() {
        if(instance==null) {
            instance = new PostgresClient();
        }
        return instance;
    }

    /**
     * Connect to Postgres with the database properties as set in System properties.
     */
    public static Connection getConnection() throws Exception {
        return getConnection(POSTGRES_DATABASE_NAME, POSTGRES_SCHEMA_NAME, POSTGRES_USERNAME, POSTGRES_PASSWORD);
    }

    /**
     * Connect to Postgres with the database properties passed in.
     *
     * This is useful when re-running component tests with the containers left up between runs and not wanting to set
     * the system properties for each test run.
     */
    public static Connection getConnection(String databaseName, String schemaName, String username, String password) throws Exception {
        String dbUrl = dbHostAndPortUrl + databaseName + "?currentSchema=" + schemaName;
        Connection connection = DriverManager.getConnection(dbUrl, username, password);
        log.debug("Connected to Postgres at: "+dbUrl);
        return connection;
    }

    /**
     * Close the Postgres connection.
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
