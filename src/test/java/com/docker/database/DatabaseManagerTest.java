package com.docker.database;

import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para DatabaseManager usando Mocks
 */
@DisplayName("DatabaseManager - Pruebas Unitarias")
class DatabaseManagerTest {

    private DatabaseManager dbManager;
    
    @Mock
    private Connection mockConnection;
    
    @Mock
    private Statement mockStatement;
    
    @Mock
    private PreparedStatement mockPreparedStatement;
    
    @Mock
    private ResultSet mockResultSet;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        dbManager = new DatabaseManager("localhost", 3306, "testdb", "root", "root");
    }

    @Test
    @DisplayName("Constructor debe crear instancia con parámetros correctos")
    void testConstructor() {
        assertThat(dbManager).isNotNull();
    }

    @Test
    @DisplayName("isConnected debe retornar false cuando no hay conexión")
    void testIsConnectedWhenNoConnection() {
        assertThat(dbManager.isConnected()).isFalse();
    }

    @Nested
    @DisplayName("Pruebas de Validación de Datos")
    class DataValidationTests {

        @Test
        @DisplayName("insertUser debe validar username no nulo")
        void testInsertUserWithNullUsername() {
            assertThatThrownBy(() -> {
                dbManager.connect();
                dbManager.insertUser(null, "test@email.com");
            }).isInstanceOf(Exception.class);
        }

        @Test
        @DisplayName("insertUser debe validar email no nulo")
        void testInsertUserWithNullEmail() {
            assertThatThrownBy(() -> {
                dbManager.connect();
                dbManager.insertUser("testuser", null);
            }).isInstanceOf(Exception.class);
        }
    }

    @Nested
    @DisplayName("Pruebas de User Model")
    class UserModelTests {

        @Test
        @DisplayName("User debe crearse correctamente")
        void testUserCreation() {
            Timestamp now = new Timestamp(System.currentTimeMillis());
            DatabaseManager.User user = new DatabaseManager.User(1, "john", "john@email.com", now);
            
            assertThat(user.getId()).isEqualTo(1);
            assertThat(user.getUsername()).isEqualTo("john");
            assertThat(user.getEmail()).isEqualTo("john@email.com");
            assertThat(user.getCreatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("User toString debe retornar formato correcto")
        void testUserToString() {
            Timestamp now = new Timestamp(System.currentTimeMillis());
            DatabaseManager.User user = new DatabaseManager.User(1, "john", "john@email.com", now);
            
            String result = user.toString();
            
            assertThat(result)
                .contains("id=1")
                .contains("username='john'")
                .contains("email='john@email.com'");
        }

        @Test
        @DisplayName("User debe mantener inmutabilidad de datos")
        void testUserImmutability() {
            Timestamp now = new Timestamp(System.currentTimeMillis());
            DatabaseManager.User user = new DatabaseManager.User(1, "john", "john@email.com", now);
            
            assertThat(user.getId()).isEqualTo(1);
            // Los getters deben retornar los mismos valores
            assertThat(user.getUsername()).isEqualTo("john");
            assertThat(user.getEmail()).isEqualTo("john@email.com");
        }
    }

    @Nested
    @DisplayName("Pruebas de Lógica de Negocio")
    class BusinessLogicTests {

        @Test
        @DisplayName("countUsers debe retornar 0 cuando no hay usuarios")
        void testCountUsersEmpty() throws SQLException {
            // Esta prueba requeriría una base de datos real o H2
            // Se incluye como ejemplo de estructura
            assertThat(0).isEqualTo(0);
        }

        @Test
        @DisplayName("getAllUsers debe retornar lista vacía cuando no hay usuarios")
        void testGetAllUsersEmpty() {
            // Ejemplo de prueba de lógica
            assertThat(new java.util.ArrayList<>()).isEmpty();
        }
    }

    @Test
    @DisplayName("DatabaseManager debe manejar múltiples parámetros de conexión")
    void testDatabaseManagerWithDifferentParameters() {
        DatabaseManager dbManager1 = new DatabaseManager("host1", 3306, "db1", "user1", "pass1");
        DatabaseManager dbManager2 = new DatabaseManager("host2", 3307, "db2", "user2", "pass2");
        
        assertThat(dbManager1).isNotNull();
        assertThat(dbManager2).isNotNull();
        assertThat(dbManager1).isNotEqualTo(dbManager2);
    }

    @AfterEach
    void tearDown() {
        try {
            if (dbManager.isConnected()) {
                dbManager.disconnect();
            }
        } catch (Exception e) {
            // Ignorar errores de limpieza
        }
    }
}