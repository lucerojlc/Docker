// SIN package, elimina la línea: package contenedor1;

import java.io.*;
import java.net.*;
import java.sql.*;

public class server {
    public static void main(String[] args) {
        // Leer variables de entorno
        String dbHost = System.getenv().getOrDefault("DB_HOST", "mysql");
        String dbUser = System.getenv().getOrDefault("DB_USER", "root");
        String dbPass = System.getenv().getOrDefault("DB_PASS", "root");
        String dbName = System.getenv().getOrDefault("DB_NAME", "sistema_telefonico");
        
        String jdbcUrl = "jdbc:mysql://" + dbHost + ":3306/" + dbName + "?allowPublicKeyRetrieval=true&useSSL=false";
        
        System.out.println("=".repeat(50));
        System.out.println("Iniciando servidor Java...");
        System.out.println("Intentando conectar a: " + jdbcUrl);
        System.out.println("=".repeat(50));
        
        Connection connection = null;
        ServerSocket serverSocket = null;
        
        try {
            // Cargar el driver de MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("✓ Driver MySQL cargado");
            
            // Esperar a que MySQL esté listo (reintentos con más tiempo)
            int maxRetries = 30;
            int retryCount = 0;
            
            while (connection == null && retryCount < maxRetries) {
                try {
                    System.out.println("Intento " + (retryCount + 1) + " de " + maxRetries + " - Conectando a MySQL...");
                    connection = DriverManager.getConnection(jdbcUrl, dbUser, dbPass);
                    System.out.println("✓ Conexión a base de datos EXITOSA!");
                } catch (SQLException e) {
                    retryCount++;
                    if (retryCount < maxRetries) {
                        System.out.println("✗ Fallo en la conexión. Reintentando en 5 segundos...");
                        Thread.sleep(5000);
                    } else {
                        throw e;
                    }
                }
            }
            
            if (connection == null) {
                throw new SQLException("No se pudo conectar a MySQL después de " + maxRetries + " intentos");
            }
            
            // Iniciar el servidor de sockets
            serverSocket = new ServerSocket(9599);
            System.out.println("=".repeat(50));
            System.out.println("✓ SERVIDOR LISTO - Esperando solicitudes en puerto 9599...");
            System.out.println("=".repeat(50));
            
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("\n>>> Cliente conectado desde: " + socket.getInetAddress());
                
                BufferedReader input = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
                String numeroTelefono = input.readLine();
                System.out.println(">>> Buscando teléfono: " + numeroTelefono);
                
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "SELECT dir_nombre, dir_direccion, ciud_nombre FROM personas "
                        + "INNER JOIN ciudades ON personas.dir_ciud_id = ciudades.ciud_id "
                        + "WHERE dir_tel = ?");
                preparedStatement.setString(1, numeroTelefono);
                ResultSet resultSet = preparedStatement.executeQuery();
                
                PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
                if (resultSet.next()) {
                    String nombre = resultSet.getString("dir_nombre");
                    String direccion = resultSet.getString("dir_direccion");
                    String ciudad = resultSet.getString("ciud_nombre");
                    String respuesta = "Nombre: " + nombre + ", Dirección: " + direccion + ", Ciudad: " + ciudad;
                    System.out.println(">>> Respuesta: " + respuesta);
                    output.println(respuesta);
                } else {
                    System.out.println(">>> No se encontró la persona");
                    output.println("Persona No Existente!!. .");
                }
                
                resultSet.close();
                preparedStatement.close();
                socket.close();
                System.out.println(">>> Conexión cerrada\n");
            }
        } catch (IOException | SQLException | ClassNotFoundException | InterruptedException e) {
            System.err.println("ERROR FATAL:");
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) connection.close();
                if (serverSocket != null) serverSocket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}