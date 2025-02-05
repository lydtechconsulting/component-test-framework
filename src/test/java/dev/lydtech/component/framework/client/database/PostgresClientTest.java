package dev.lydtech.component.framework.client.database;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.sameInstance;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

public class PostgresClientTest {
    private MockedStatic<DriverManager> mockedDriverManager;

    @BeforeEach
    void setup() {
        System.setProperty("postgres.mapped.port", "5432");

        mockedDriverManager = mockStatic(DriverManager.class);
    }

    @AfterEach
    void tearDown() {
        System.clearProperty("postgres.mapped.port");

        mockedDriverManager.close();
    }

    @Test
    void testSingletonInstance() {
        PostgresClient instance1 = PostgresClient.getInstance();
        PostgresClient instance2 = PostgresClient.getInstance();

        assertThat(instance1, is(sameInstance(instance2)));
    }

    @Test
    void testGetConnectionSuccess() throws Exception {
        String dbUrl = "jdbc:postgresql://localhost:5432/testdb?currentSchema=testschema";
        String username = "user";
        String password = "password";
        Connection mockConnection = mock(Connection.class);

        mockedDriverManager.when(() -> DriverManager.getConnection(dbUrl, username, password))
                .thenReturn(mockConnection);

        Connection connection = PostgresClient.getInstance().getConnection("testdb", "testschema", username, password);

        assertThat(connection, is(notNullValue()));
        mockedDriverManager.verify(() -> DriverManager.getConnection(dbUrl, username, password));
    }

    @Test
    void testGetConnectionThrowsSQLException() {
        String dbUrl = "jdbc:postgresql://localhost:5432/invalid_db?currentSchema=invalid_schema";
        String username = "user";
        String password = "password";

        mockedDriverManager.when(() -> DriverManager.getConnection(dbUrl, username, password))
                .thenThrow(SQLException.class);

        assertThrows(SQLException.class, () -> {
            PostgresClient.getInstance().getConnection("invalid_db", "invalid_schema", username, password);
        });
    }

    @Test
    void testCloseConnectionSuccessfully() throws Exception {
        Connection mockConnection = mock(Connection.class);
        when(mockConnection.isClosed()).thenReturn(false);

        PostgresClient.getInstance().close(mockConnection);

        verify(mockConnection).close();
    }

    @Test
    void testCloseConnectionAlreadyClosed() throws Exception {
        Connection mockConnection = mock(Connection.class);
        when(mockConnection.isClosed()).thenReturn(true);

        PostgresClient.getInstance().close(mockConnection);

        verify(mockConnection, never()).close();
    }

    @Test
    void testGetDbHostAndPortUrl() {
        String dbUrl = PostgresClient.getInstance().getDbHostAndPortUrl();

        assertThat(dbUrl, is("jdbc:postgresql://localhost:5432/"));
    }
}
