// SIN package, elimina la línea: package contenedor2;

import java.io.*;
import java.net.*;

public class Cliente {
    public static void main(String[] args) {
        String serverHost = System.getenv().getOrDefault("SERVER_HOST", "servidor");
        String serverPort = System.getenv().getOrDefault("SERVER_PORT", "9599");
        
        System.out.println("=".repeat(50));
        System.out.println("Cliente Java - Sistema Telefónico");
        System.out.println("Conectando a: " + serverHost + ":" + serverPort);
        System.out.println("=".repeat(50));
        
        try {
            Socket socket = new Socket(serverHost, Integer.parseInt(serverPort));
            System.out.println("✓ Conectado al servidor exitosamente!\n");
            
            BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Ingrese el número de teléfono: ");
            String numeroTelefono = consoleInput.readLine();
           
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
            output.println(numeroTelefono);
          
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String respuesta = input.readLine();
            
            System.out.println("\n" + "=".repeat(50));
            System.out.println("RESPUESTA DEL SERVIDOR:");
            System.out.println(respuesta);
            System.out.println("=".repeat(50));
            
            socket.close();
            System.out.println("\n✓ Conexión cerrada.");
            
        } catch (UnknownHostException e) {
            System.err.println("✗ Error: No se pudo encontrar el servidor '" + serverHost + "'");
            System.err.println("Verifica que el servicio 'servidor' esté corriendo.");
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("✗ Error de conexión con el servidor.");
            e.printStackTrace();
        }
    }
}