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
    

    public client( String host, int port ) {
        try {
            System.out.println("Connecting to " + host + " on port " + port);
            socket = new Socket(host, port);
            System.out.println("Just connected to " + socket.getRemoteSocketAddress());

            // creaFlujos();

            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();

            System.out.println("Enter username: ");

            BufferedReader username = new BufferedReader(new InputStreamReader(System.in));
            String x = username.readLine();
            System.out.println(x);
            byte[] name = x.getBytes();
            outputStream.write(name);
            


            while (socket.isConnected()) {
                try {

                    
                    System.out.println("Enter message: ");
                    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
                    String msg = in.readLine();
                    byte[] message = msg.getBytes();
                    outputStream.write(message);

                    // after 1 second, read messages from server
                    Thread.sleep(1000);
                    byte[] buffer = new byte[1024];
                    int count = inputStream.read(buffer);
                    String msgFromServer = new String(buffer, 0, count);
                    System.out.println("Message from server: " + msgFromServer);
                    
                    // Restart loop after 1 second of inactivity
                    
                    

                } catch (Exception e) {
                    // TODO: handle exception
                }
                    





            }            
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
