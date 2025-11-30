package com.docker.database;

import org.junit.jupiter.api.*;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DatabaseManagerIntegrationTest {

    @Container
    private static final MySQLContainer<?> mysqlContainer =
            new MySQLContainer<>("mysql:8.0")
                    .withDatabaseName("testdb")
                    .withUsername("testuser")
                    .withPassword("testpass");

    private static DatabaseManager databaseManager;

    @BeforeAll
    static void setup() throws SQLException {
        mysqlContainer.start();

        databaseManager = new DatabaseManager(
                mysqlContainer.getJdbcUrl(),
                mysqlContainer.getUsername(),
                mysqlContainer.getPassword()
        );

        databaseManager.connect();
    }

    @AfterAll
    static void tearDown() {
        mysqlContainer.stop();
    }

    @Test
    @Order(1)
    void testInsertUser() {
        boolean response = databaseManager.insertUser("Lucero", "lucero@test.com");
        assertTrue(response, "El usuario debería insertarse");
    }

    @Test
    @Order(2)
    void testSelectUser() throws SQLException {
        ResultSet rs = databaseManager.getUserByEmail("lucero@test.com");
        assertNotNull(rs);

        boolean exists = false;
        while (rs.next()) {
            exists = true;
            assertEquals("Lucero", rs.getString("name"));
        }
        assertTrue(exists, "Se debe encontrar el usuario insertado");
    }
}
