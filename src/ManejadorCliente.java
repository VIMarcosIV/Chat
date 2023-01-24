import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ManejadorCliente implements Runnable {

    public static ArrayList<ManejadorCliente> clientes = new ArrayList<>();
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String nombreUsuario;


    // Cuando tenemos un socket podemos leer y escribir:
    // Cuando se lee se necesita un BufferedReader
    // InputStreamReader es el input de donde va a leer el BufferedReader para que no lea caracter a caracter

    // Cuando se escribe se necesita un BufferedWriter
    // OutputStreamReader es el output de donde se va ha escribir el BufferedWriter
    public ManejadorCliente(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.nombreUsuario = bufferedReader.readLine();
            // Esta línea añadirá la lista de clientes el cliente que se acaba de conectar
            this.clientes.add(this);
            broadcastMensaje("Server: " + nombreUsuario + " ha entrado al chat");
        } catch (IOException e) {
            cierraTodo(socket, bufferedWriter, bufferedReader);
            System.out.println(e.getMessage());
        }
    }

    // Método para enviar mensajes a los usuarios que han entrado al chat
    private void broadcastMensaje(String mensaje) {
        for (ManejadorCliente manejadorCliente : clientes) {
            try {
                if (!manejadorCliente.nombreUsuario.equals(nombreUsuario)) {
                    manejadorCliente.bufferedWriter.write(mensaje);
                    manejadorCliente.bufferedWriter.newLine();
                    // flush() == "cambio" en un walkie para que el otro buffer pueda leer
                    manejadorCliente.bufferedWriter.flush();
                }
            } catch (IOException e) {
                cierraTodo(socket, bufferedWriter, bufferedReader);
                System.out.println(e.getMessage());
            }
        }
    }

    private void cierraTodo(Socket socket, BufferedWriter bufferedWriter, BufferedReader bufferedReader) {
        removeCliente();
        try {
            if (bufferedReader != null) bufferedReader.close();
            if (bufferedWriter != null) bufferedWriter.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    // Método encargado de borrar un cliente del chat
    public void removeCliente() {
        System.out.println("removeCliente");
        System.out.println("Número de personas en el chat: " + clientes.size());
        clientes.remove(this);
        System.out.println("Número de personas en el chat: " + clientes.size());
        broadcastMensaje("Server: " + nombreUsuario + " ha salido del chat");
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    @Override
    public void run() {
        String mensajeDesdeCliente;
        while (socket.isConnected()) {
            try {
                mensajeDesdeCliente = bufferedReader.readLine();
                broadcastMensaje(mensajeDesdeCliente);
            } catch (IOException e) {
                cierraTodo(socket, bufferedWriter, bufferedReader);
            }
        }
    }
}
