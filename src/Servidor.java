import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLOutput;

public class Servidor {

    // Un Socket es un mecanismo ideado para la comunicación entre un programa del servidor y un programa del cliente en una red.
    // Es decir, es el túnel/tubería encaragada de conectar al cliente con el servidor
    private ServerSocket serverSocket;

    public Servidor(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void arrancaServidor() {
        try {
            while (!serverSocket.isClosed()) {

                // El socket se quedara esperando a que un cliente desde fuera se intente la conexión
                Socket socket = serverSocket.accept();

                // Clase encargada de conectar al cliente
                ManejadorCliente cliente = new ManejadorCliente(socket);
                System.out.println("Cliente conectado: " + cliente.getNombreUsuario());

                // Arrancamos el hilo
                Thread thread = new Thread(cliente);
                thread.start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void cerrarServerSocket(){
        try{
            if(serverSocket != null){
                serverSocket.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket1 = new ServerSocket(9999);
        Servidor server = new Servidor(serverSocket1);
        server.arrancaServidor();
    }

}
