package contenedor1;

import java.io.*;
import java.net.*;
import java.sql.*;

public class server {

    public static void main(String[] args) {
        try {
           
            ServerSocket serverSocket = new ServerSocket(9599);
            System.out.println("Servidor Esperando Solicitud...");

            
            Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/sistema_telefonico", "root", "");

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Cliente conectado,Conexion Exitosa");

                
                BufferedReader input = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
                String numeroTelefono = input.readLine();

                
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

                    System.out.println("Nombre: " + nombre + ", Dirección: " + direccion + ", Ciudad: " + ciudad);
                    output.println("Nombre: " + nombre + ", Dirección: " + direccion + ", Ciudad: " + ciudad);
                } else {
                    output.println("Persona No Existente!!. .");
                }

                socket.close();
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }
}
