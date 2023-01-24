import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.time.LocalTime;
import java.util.Scanner;

public class Cliente {

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String nombreUsuario;

    public Cliente(Socket socket, String nombreUsuario) {
        this.socket = socket;
        this.nombreUsuario = nombreUsuario;
        try {
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            cierraTodo(socket, bufferedWriter, bufferedReader);
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Introduce tu nick de usuario para entrar al chat: ");
        String username = sc.nextLine();
        try {
            Socket socket1 = new Socket("192.168.50.70", 9999);
            Cliente cliente = new Cliente(socket1, username);
            cliente.esperandoMensaje();
            cliente.mandaMensaje();
        } catch (UnknownHostException u) {
            u.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void esperandoMensaje() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String mensajeChat;
                try {
                    while (socket.isConnected()) {
                        mensajeChat = bufferedReader.readLine();
                    }
                } catch (IOException e) {
                    cierraTodo(socket, bufferedWriter, bufferedReader);
                }
            }
        }).start();
    }

    private void mandaMensaje() {
        try {
            bufferedWriter.write(nombreUsuario);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            Scanner sc = new Scanner(System.in);

            while (socket.isConnected()) {
                String mensaje = sc.nextLine();
                if (mensaje.equals("SALIR")) System.exit(0);

                bufferedWriter.write(LocalTime.now() + " " + ": " + mensaje);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        } catch (IOException e) {
            cierraTodo(socket, bufferedWriter, bufferedReader);
        }
    }

    private void cierraTodo(Socket socket, BufferedWriter bufferedWriter, BufferedReader bufferedReader) {
        try {
            if (bufferedReader != null) bufferedReader.close();
            if (bufferedWriter != null) bufferedWriter.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

}
