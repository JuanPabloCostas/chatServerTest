import java.io.*;
import java.net.*;

import javax.swing.JButton;
import javax.swing.JFrame;


public class client {

     
    private ChatGUI chat;
    private Socket socket;
    private OutputStream outputStream;

    //Funcion para que el cliente conecte con el servidor

    public void conectarServidor(String host, int port) {
        try {
            System.out.println("Connecting to " + host + " on port " + port);
            socket = new Socket(host, port);
            System.out.println("Just connected to " + socket.getRemoteSocketAddress());

            outputStream = socket.getOutputStream();

            System.out.println("Enter username: ");

            BufferedReader username = new BufferedReader(new InputStreamReader(System.in));
            String x = username.readLine();
            System.out.println(x);
            byte[] name = x.getBytes();
            outputStream.write(name);




        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    } // end conectarServidor

    public void enviarMensaje(ChatGUI chat, Socket socket) {
        try {
            OutputStream outputStream = socket.getOutputStream();
            chat.tf.addActionListener(e -> {
                String message = chat.tf.getText();
                if (message.equals("END")) {
                    System.exit(0);
                    
                }
                chat.messages.append(chat.tf.getText() + "\n");
                chat.tf.setText("");

                try {
                    byte[] msg = message.getBytes();
                    outputStream.write(msg);
                } catch (Exception ex) {
                    System.out.println("Error: " + ex);
                }

            });


            
            
            
            
            // OutputStream outputStream = socket.getOutputStream();
            // System.out.println("Enter message: ");
            // // Get from chatGUI
            // BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            // String msg = in.readLine();
            // byte[] message = msg.getBytes();
            // outputStream.write(message);
            


        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

    public void recibirMensaje(ChatGUI chat, Socket socket) {
        try {
            InputStream inputStream = socket.getInputStream();
            byte[] buffer = new byte[1024];
            inputStream.read(buffer);
            String msg = new String(buffer);
            msg = msg.trim();
            if (msg.startsWith("LIST")) {
                msg = msg.substring(4);
                chat.users.removeAll();
                String[] userList = msg.split("#");
                for (int i = 0; i < userList.length; i++) {
                    userList[i] = userList[i].trim();
                }
                OutputStream outputStream = socket.getOutputStream();
                for (String user : userList) {
                    JButton userButton = new JButton(user);
                    // button to establish private chat
                    userButton.addActionListener(e -> {
                        try {
                            byte[] msg2 = ("REQUEST#" + user).getBytes();
                            outputStream.write(msg2);
                        } catch (Exception ex) {
                            System.out.println("Error: " + ex);
                        }
                    });

                    chat.users.add(userButton);
                }
                chat.users.revalidate();


                
            }
            // else if (msg.startsWith("CONREQ")) {
            //     chat.requestFrame = new JFrame("Request from " + msg.substring(6));
            //     chat.requestFrame.setSize(300, 100);
            //     chat.requestFrame.setVisible(true);
            //     JButton accept = new JButton("Accept");
            //     JButton decline = new JButton("Decline");
            //     accept.addActionListener(e -> {
            //         try {
            //             byte[] msg2 = ("CONACC#" + msg.substring(6)).getBytes();
            //             outputStream.write(msg2);
            //             chat.requestFrame.dispose();
            //         } catch (Exception ex) {
            //             System.out.println("Error: " + ex);
            //         }
            //     });
            // }
            
            else{
                chat.addMessage(msg);
            }
            
            
            // System.out.println(msg);
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

    public client(String host, int port) {
        try {

            conectarServidor(host, port);

            // Start ChatGUI.java
            chat = new ChatGUI();
            chat.addMessage("This is a test message.");



            Thread enviar = new Thread() {
                public void run() {
                    try {
                        if (socket.isConnected()) {
                            enviarMensaje(chat, socket);
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
                            recibirMensaje(chat, socket);
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
