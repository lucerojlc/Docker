package com.docker.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase para gestionar la conexión y operaciones con MySQL
 */
public class DatabaseManager {
    
    private final String url;
    private final String username;
    private final String password;
    private Connection connection;

    public DatabaseManager(String host, int port, String database, String username, String password) {
        this.url = String.format("jdbc:mysql://%s:%d/%s", host, port, database);
        this.username = username;
        this.password = password;
    }

    /**
     * Establece la conexión con la base de datos
     */
    public void connect() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(url, username, password);
        }
    }

    /**
     * Cierra la conexión con la base de datos
     */
    public void disconnect() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    /**
     * Verifica si la conexión está activa
     */
    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed() && connection.isValid(2);
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Crea una tabla de usuarios
     */
    public void createUsersTable() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS users (
                id INT PRIMARY KEY AUTO_INCREMENT,
                username VARCHAR(50) NOT NULL UNIQUE,
                email VARCHAR(100) NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
            """;
        
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }

    /**
     * Inserta un usuario en la base de datos
     */
    public int insertUser(String username, String email) throws SQLException {
        String sql = "INSERT INTO users (username, email) VALUES (?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, username);
            pstmt.setString(2, email);
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Inserción falló, no se afectaron filas.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Inserción falló, no se obtuvo ID.");
                }
            }
        }
    }

    /**
     * Obtiene un usuario por su ID
     */
    public User getUserById(int id) throws SQLException {
        String sql = "SELECT * FROM users WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getTimestamp("created_at")
                    );
                }
            }
        }
        return null;
    }

    /**
     * Obtiene todos los usuarios
     */
    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                users.add(new User(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("email"),
                    rs.getTimestamp("created_at")
                ));
            }
        }
        return users;
    }

    /**
     * Actualiza un usuario
     */
    public boolean updateUser(int id, String username, String email) throws SQLException {
        String sql = "UPDATE users SET username = ?, email = ? WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, email);
            pstmt.setInt(3, id);
            
            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Elimina un usuario
     */
    public boolean deleteUser(int id) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Cuenta el número de usuarios
     */
    public int countUsers() throws SQLException {
        String sql = "SELECT COUNT(*) as total FROM users";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt("total");
            }
        }
        return 0;
    }

    /**
     * Clase interna User
     */
    public static class User {
        private final int id;
        private final String username;
        private final String email;
        private final Timestamp createdAt;

        public User(int id, String username, String email, Timestamp createdAt) {
            this.id = id;
            this.username = username;
            this.email = email;
            this.createdAt = createdAt;
        }

        public int getId() { return id; }
        public String getUsername() { return username; }
        public String getEmail() { return email; }
        public Timestamp getCreatedAt() { return createdAt; }

        @Override
        public String toString() {
            return String.format("User{id=%d, username='%s', email='%s', createdAt=%s}",
                    id, username, email, createdAt);
        }
    }
}