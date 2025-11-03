package contenedor2;

import java.io.*;
import java.net.*;

public class Cliente {

    public static void main(String[] args) {
        try {
        
            Socket socket = new Socket("localhost", 9599);

            
            BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Ingrese el número de teléfono..: ");
            String numeroTelefono = consoleInput.readLine();

           
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
            output.println(numeroTelefono);

          
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String respuesta = input.readLine();
            System.out.println("Respuesta desde el servidor: " + respuesta);

            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

