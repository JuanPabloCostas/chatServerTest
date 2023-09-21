import java.io.*;
import java.net.*;

public class client {

    private Socket socket;
    private InputStream netIn;
    private OutputStream netOut;


    public void creaFlujos() {
        try {
            
            netIn = new DataInputStream(socket.getInputStream());
            netOut = new DataOutputStream(socket.getOutputStream());
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

    public void enviarMensaje() {
        try {
            OutputStream outputStream = socket.getOutputStream();
            System.out.println("Enter message: ");
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            String msg = in.readLine();
            byte[] message = msg.getBytes();
            outputStream.write(message);
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }
    

    public client( String host, int port ) {
        try {
            System.out.println("Connecting to " + host + " on port " + port);
            socket = new Socket(host, port);
            System.out.println("Just connected to " + socket.getRemoteSocketAddress());

            // Create input and output streams to read from and write to the server

            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();

            System.out.println("Enter username: ");

            BufferedReader username = new BufferedReader(new InputStreamReader(System.in));
            String x = username.readLine();
            System.out.println(x);
            byte[] name = x.getBytes();
            outputStream.write(name);

            Thread enviar = new Thread() {
                        public void run() {
                            try {
                                while (socket.isConnected()) {
                                    enviarMensaje();
                                }
                            } catch (Exception e) {
                                System.out.println("Error: " + e);
                            }
                        }
                    };

            enviar.start();

            Thread recibir = new Thread() {
                        public void run() {
                            try {
                                while (socket.isConnected()) {
                                    byte[] buffer = new byte[1024];
                                    inputStream.read(buffer);
                                    String msg = new String(buffer);
                                    System.out.println(msg);
                                }
                            } catch (Exception e) {
                                System.out.println("Error: " + e);
                            }
                        }
                    };

            recibir.start();
             
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
        

    }
    
    public static void main(String[] args) {
        // Check args
        if (args.length != 2) {
            System.out.println("Usage: java client <host> <port>");
            System.exit(1);
        }
        new client(args[0], Integer.parseInt(args[1]));

        
    }
}
