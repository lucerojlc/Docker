<<<<<<< HEAD
package com.docker.database;

import org.junit.jupiter.api.*;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * Pruebas de integración usando Testcontainers con MySQL real
 */
@Testcontainers
@DisplayName("DatabaseManager - Pruebas de Integración")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DatabaseManagerIntegrationTest {

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass")
            .withReuse(false);

    private static DatabaseManager dbManager;

    @BeforeAll
    static void setUpAll() throws SQLException {
        // Esperar a que el contenedor esté listo
        mysql.start();
        
        dbManager = new DatabaseManager(
                mysql.getHost(),
                mysql.getFirstMappedPort(),
                mysql.getDatabaseName(),
                mysql.getUsername(),
                mysql.getPassword()
        );
        
        dbManager.connect();
        dbManager.createUsersTable();
    }

    @AfterAll
    static void tearDownAll() throws SQLException {
        if (dbManager != null) {
            dbManager.disconnect();
        }
    }

    @BeforeEach
    void setUp() throws SQLException {
        // Limpiar la tabla antes de cada prueba
        cleanUsersTable();
    }

    private void cleanUsersTable() throws SQLException {
        List<DatabaseManager.User> users = dbManager.getAllUsers();
        for (DatabaseManager.User user : users) {
            dbManager.deleteUser(user.getId());
        }
    }

    @Test
    @Order(1)
    @DisplayName("Conexión a base de datos debe ser exitosa")
    void testDatabaseConnection() {
        assertThat(dbManager.isConnected()).isTrue();
    }

    @Test
    @Order(2)
    @DisplayName("Debe crear tabla users correctamente")
    void testCreateUsersTable() throws SQLException {
        // La tabla ya fue creada en @BeforeAll
        // Verificamos que podemos insertar datos
        int userId = dbManager.insertUser("testuser", "test@email.com");
        assertThat(userId).isGreaterThan(0);
    }

    @Test
    @Order(3)
    @DisplayName("Debe insertar usuario correctamente")
    void testInsertUser() throws SQLException {
        int userId = dbManager.insertUser("john_doe", "john@example.com");
        
        assertThat(userId).isGreaterThan(0);
        
        DatabaseManager.User user = dbManager.getUserById(userId);
        assertThat(user).isNotNull();
        assertThat(user.getUsername()).isEqualTo("john_doe");
        assertThat(user.getEmail()).isEqualTo("john@example.com");
    }

    @Test
    @Order(4)
    @DisplayName("Debe obtener usuario por ID correctamente")
    void testGetUserById() throws SQLException {
        // Insertar usuario
        int userId = dbManager.insertUser("jane_doe", "jane@example.com");
        
        // Obtener usuario
        DatabaseManager.User user = dbManager.getUserById(userId);
        
        assertThat(user).isNotNull();
        assertThat(user.getId()).isEqualTo(userId);
        assertThat(user.getUsername()).isEqualTo("jane_doe");
        assertThat(user.getEmail()).isEqualTo("jane@example.com");
        assertThat(user.getCreatedAt()).isNotNull();
    }

    @Test
    @Order(5)
    @DisplayName("Debe retornar null cuando usuario no existe")
    void testGetUserByIdNotFound() throws SQLException {
        DatabaseManager.User user = dbManager.getUserById(99999);
        assertThat(user).isNull();
    }

    @Test
    @Order(6)
    @DisplayName("Debe obtener todos los usuarios correctamente")
    void testGetAllUsers() throws SQLException {
        // Insertar varios usuarios
        dbManager.insertUser("user1", "user1@email.com");
        dbManager.insertUser("user2", "user2@email.com");
        dbManager.insertUser("user3", "user3@email.com");
        
        List<DatabaseManager.User> users = dbManager.getAllUsers();
        
        assertThat(users).hasSize(3);
        assertThat(users)
                .extracting(DatabaseManager.User::getUsername)
                .containsExactlyInAnyOrder("user1", "user2", "user3");
    }

    @Test
    @Order(7)
    @DisplayName("Debe actualizar usuario correctamente")
    void testUpdateUser() throws SQLException {
        // Insertar usuario
        int userId = dbManager.insertUser("old_username", "old@email.com");
        
        // Actualizar usuario
        boolean updated = dbManager.updateUser(userId, "new_username", "new@email.com");
        
        assertThat(updated).isTrue();
        
        // Verificar actualización
        DatabaseManager.User user = dbManager.getUserById(userId);
        assertThat(user.getUsername()).isEqualTo("new_username");
        assertThat(user.getEmail()).isEqualTo("new@email.com");
    }

    @Test
    @Order(8)
    @DisplayName("Debe retornar false al actualizar usuario inexistente")
    void testUpdateNonExistentUser() throws SQLException {
        boolean updated = dbManager.updateUser(99999, "username", "email@email.com");
        assertThat(updated).isFalse();
    }

    @Test
    @Order(9)
    @DisplayName("Debe eliminar usuario correctamente")
    void testDeleteUser() throws SQLException {
        // Insertar usuario
        int userId = dbManager.insertUser("to_delete", "delete@email.com");
        
        // Eliminar usuario
        boolean deleted = dbManager.deleteUser(userId);
        
        assertThat(deleted).isTrue();
        
        // Verificar que no existe
        DatabaseManager.User user = dbManager.getUserById(userId);
        assertThat(user).isNull();
    }

    @Test
    @Order(10)
    @DisplayName("Debe retornar false al eliminar usuario inexistente")
    void testDeleteNonExistentUser() throws SQLException {
        boolean deleted = dbManager.deleteUser(99999);
        assertThat(deleted).isFalse();
    }

    @Test
    @Order(11)
    @DisplayName("Debe contar usuarios correctamente")
    void testCountUsers() throws SQLException {
        assertThat(dbManager.countUsers()).isEqualTo(0);
        
        dbManager.insertUser("user1", "user1@email.com");
        assertThat(dbManager.countUsers()).isEqualTo(1);
        
        dbManager.insertUser("user2", "user2@email.com");
        assertThat(dbManager.countUsers()).isEqualTo(2);
        
        dbManager.insertUser("user3", "user3@email.com");
        assertThat(dbManager.countUsers()).isEqualTo(3);
    }

    @Test
    @Order(12)
    @DisplayName("No debe permitir username duplicado")
    void testDuplicateUsername() {
        assertThatThrownBy(() -> {
            dbManager.insertUser("duplicate", "email1@email.com");
            dbManager.insertUser("duplicate", "email2@email.com");
        }).isInstanceOf(SQLException.class)
          .hasMessageContaining("Duplicate entry");
    }

    @Test
    @Order(13)
    @DisplayName("Debe manejar transacciones CRUD completas")
    void testFullCRUDCycle() throws SQLException {
        // Create
        int userId = dbManager.insertUser("crud_user", "crud@email.com");
        assertThat(userId).isGreaterThan(0);
        
        // Read
        DatabaseManager.User user = dbManager.getUserById(userId);
        assertThat(user).isNotNull();
        assertThat(user.getUsername()).isEqualTo("crud_user");
        
        // Update
        boolean updated = dbManager.updateUser(userId, "updated_user", "updated@email.com");
        assertThat(updated).isTrue();
        
        user = dbManager.getUserById(userId);
        assertThat(user.getUsername()).isEqualTo("updated_user");
        
        // Delete
        boolean deleted = dbManager.deleteUser(userId);
        assertThat(deleted).isTrue();
        
        user = dbManager.getUserById(userId);
        assertThat(user).isNull();
    }
}
=======
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
>>>>>>> 6a312f22d09697e7b7d2675535161f7a485c0864
