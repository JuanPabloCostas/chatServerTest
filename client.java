import java.awt.*;
import java.io.*;
import java.net.*;

import javax.swing.*;


public class client {

     
    private ChatGUI chat;
    private serverp2p serverp2p;
    private Socket socket;
    private OutputStream outputStream;
    public static String hoster;

    //Funcion para que el cliente conecte con el servidor

    public void conectarServidor(String host, int port) {
        try {
            System.out.println("Connecting to " + host + " on port " + port);
            socket = new Socket(host, port);
            System.out.println("Just connected to " + socket.getRemoteSocketAddress());

            outputStream = socket.getOutputStream();

            System.out.println("Enter username: ");

            // Open window for username
            JFrame username = new JFrame("Username");
            username.setSize(300, 300);
            username.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            username.setLayout(null);

            // Label for username
            JLabel label = new JLabel("Enter username:");
            label.setBounds(50, 0, 200, 50);
            username.add(label);


            // Text field for username
            JTextField tf = new JTextField(10);
            tf.setBounds(50, 50, 200, 50);
            tf.setBackground(new Color(140, 240, 240));
            username.add(tf);

            // Button to send username
            JButton send = new JButton("Send");
            send.setBounds(50, 100, 100, 50);

            send.addActionListener(e -> {
                try {
                    String user = tf.getText();
                    hoster = user;
                    byte[] name = user.getBytes();
                    outputStream.write(name);
                    username.dispose();
                    begin();
                } catch (Exception ex) {
                    System.out.println("Error: " + ex);
                    System.out.println("1");
                }
            });

            username.add(send);

            
            username.setVisible(true);




            // BufferedReader username = new BufferedReader(new InputStreamReader(System.in));
            // String x = username.readLine();
            // System.out.println(x);
            // hoster = x;
            // byte[] name = x.getBytes();
            // outputStream.write(name);




        } catch (Exception e) {
            System.out.println("Error: " + e);
            System.out.println("2");
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
                    System.out.println("3");
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
            System.out.println("4");
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
                chat.users.setVisible(false);
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
                
                chat.users.setVisible(true);


                
            }
            else if (msg.startsWith("CONREQ")) {
                msg = msg.substring(7);
                // Get username
                // example message: eevee wants to connect with you
                String[] msgSplit = msg.split(" ");
                String user = msgSplit[0];
                System.out.println(user);

                // Create new window for accept or reject request
                JFrame request = new JFrame("Request from " + user);
                request.setSize(300, 300);
                request.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                request.setLayout(null);
                request.setVisible(true);

                // Accept button
                JButton accept = new JButton("Accept");
                accept.setBounds(50, 50, 100, 50);
                accept.addActionListener(e -> {
                    try {

                        // Create new socekt for private chat
                        
                        
                        serverp2p = new serverp2p(user, null, hoster);
                        
                        System.out.println("here");
                        
                        
                        int port = serverp2p.port;





                        byte[] msg2 = ("CONACC# " + user + "%" + port + "%" + hoster).getBytes();
                        outputStream.write(msg2);
                        request.dispose();

                        


                    } catch (Exception ex) {
                        System.out.println("Error: " + ex);
                    }
                });
                request.add(accept);

                // Reject button
                JButton reject = new JButton("Reject");
                reject.setBounds(150, 50, 100, 50);
                reject.addActionListener(e -> {
                    try {
                        byte[] msg2 = ("CONREJ# " + user).getBytes();
                        outputStream.write(msg2);
                        request.dispose();
                    } catch (Exception ex) {
                        System.out.println("Error: " + ex);
                    }
                });
                request.add(reject);



            }
            else if (msg.startsWith("CONACC#")) {
                msg = msg.substring(8);
                String[] msgSplit = msg.split("%");
                String user = msgSplit[2];
                int port = Integer.parseInt(msgSplit[1]);

                // Create new socket for private chat
                Socket socket2 = new Socket("localhost", port);
                serverp2p = new serverp2p(user, socket2, hoster);

            }
            else if (msg.startsWith("CONREJ")) {
                msg = msg.substring(7);
                String[] msgSplit = msg.split("%");
                String user = msgSplit[0];
                System.out.println(user);
                System.out.println("Request rejected");
                
            }
            
            else{
                chat.addMessage(msg);
            }
            
            
            // System.out.println(msg);
        } catch (Exception e) {
            System.out.println("Error: " + e);
            // System.out.println("5 error listen");
            JOptionPane.showMessageDialog(null, "Server disconnected", "Error", JOptionPane.ERROR_MESSAGE);
            
            System.exit(1);
            


            
        }
    }

    public void begin() {
        try {
            // Start ChatGUI.java
            chat = new ChatGUI(hoster);
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
                        System.out.println("Error on socket");
                    }
                }
            };

            recibir.start();

        } catch (Exception e) {
            System.out.println("Error: " + e);
            System.out.println("error on socket");
        }
    }

    public client(String host, int port) {
        try {

            conectarServidor(host, port);

            


        //     // Start ChatGUI.java
        //     chat = new ChatGUI(hoster);
        //     chat.addMessage("This is a test message.");



        //     Thread enviar = new Thread() {
        //         public void run() {
        //             try {
        //                 if (socket.isConnected()) {
        //                     enviarMensaje(chat, socket);
        //                 }
        //             } catch (Exception e) {
        //                 System.out.println("Error: " + e);
        //             }
        //         }
        //     };

        //     enviar.start();

        //     Thread recibir = new Thread() {
        //         public void run() {
        //             try {
        //                 while (socket.isConnected()) {
        //                     recibirMensaje(chat, socket);
        //                 }
        //             } catch (Exception e) {
        //                 System.out.println("Error: " + e);
        //             }
        //         }
        //     };

        //     recibir.start();

        } catch (Exception e) {
            if (!socket.isConnected()) {
                System.exit(1);
            }
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
