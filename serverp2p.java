import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.nio.file.Files;

import javax.swing.*;

public class serverp2p {

    public int port;
    private String path;
    public JFrame privateChat;

    public void serverp2pThread(Socket socket, String user, String host) {

        // create or select a path to store user files
        // create a folder with the name of the user, if not exists

        try {
            String userHome = System.getProperty("user.home");
            path = userHome + File.separator + "Desktop" + File.separator + "ChatFiles" + File.separator + host + File.separator;
        
            File storage = new File(path);
            if (!storage.exists()) {
                storage.mkdirs();
            }
        
            System.out.println("Path: " + path);
        
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
        
        try {
            JFrame privateChat = new JFrame("Private Chat with " + user);
            privateChat.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            privateChat.setSize(700, 700);

            // Crear un panel principal con BorderLayout
            JPanel mainPanel = new JPanel(new BorderLayout());

            JTextArea messages = new JTextArea();
            messages.setEditable(false);

            // Agregar un JScrollPane al área de mensajes
            JScrollPane messagesScrollPane = new JScrollPane(messages);
            mainPanel.add(messagesScrollPane, BorderLayout.CENTER);

            // Crear el panel de entrada
            JPanel input = new JPanel();
            input.setBackground(new Color(140, 140, 240));

            JTextField tf = new JTextField(20);

            OutputStream outputStream = socket.getOutputStream();
            tf.addActionListener(e -> {
                String message = tf.getText();
                if (message.equals("END")) {
                    try {
                        byte[] msg = "END".getBytes();
                        outputStream.write(msg);
                        socket.close();
                        privateChat.dispose();
                    } catch (Exception ex) {
                        System.out.println("Error: " + ex);
                    }
                }
                messages.append(tf.getText() + "\n");
                tf.setText("");

                try {
                    byte[] msg = ("[" + host + "] -> " + message).getBytes();
                    outputStream.write(msg);
                } catch (Exception ex) {
                    System.out.println("Error: " + ex);
                }
            });

            input.add(tf);

            // Agregar el botón "Choose file" al panel de entrada
            JButton choose = new JButton("Choose file");
            choose.setBackground(new Color(140, 240, 240));

            choose.addActionListener(e -> {
                Frame frame = new Frame(); // Puedes pasar el marco principal de tu aplicación aquí

                FileDialog fileDialog = new FileDialog(frame, "Select File", FileDialog.LOAD);
                fileDialog.setVisible(true);

                String directory = fileDialog.getDirectory();
                String file = fileDialog.getFile();

                if (directory != null && file != null) {
                    String filePath = directory + file;
                    System.out.println("Selected file: " + filePath);

                    // Envía un mensaje de archivo entrante
                    try {
                        byte[] msg = ("PUTFILE " + file).getBytes();
                        outputStream.write(msg);

                        // Envía el archivo
                        File fileToSend = new File(filePath);
                        byte[] fileBytes = Files.readAllBytes(fileToSend.toPath());
                        outputStream.write(fileBytes);

                    } catch (Exception ex) {
                        System.out.println("Error: " + ex);
                    }
                }
            });

            input.add(choose);

            mainPanel.add(input, BorderLayout.SOUTH);

            privateChat.add(mainPanel);
            privateChat.setVisible(true);

            



            while (socket.isConnected()) {
                InputStream inputStream = socket.getInputStream();
                byte[] data = new byte[1024];
                inputStream.read(data);
                String message = new String(data);
                message = message.trim();

                if (message.equals("END")) {
                    socket.close();
                    privateChat.dispose();
                    break;
                } else if (message.startsWith("PUTFILE")) {

                    // get file name
                    String fileName = message.substring(8);

                    // check if file exists
                    File file = new File(path + fileName);
                    if (file.exists()) {
                        file.delete();
                    }

                    // receive file
                    InputStream is = socket.getInputStream();

                    FileOutputStream fos = new FileOutputStream(path + fileName);
                    BufferedOutputStream bos = new BufferedOutputStream(fos);

                    byte[] bytes = new byte[1024];
                    int count;
                    while ((count = is.read(bytes)) >= 0) {
                        bos.write(bytes, 0, count);
                        System.out.println("count: " + count);
                        if (count < 1024) {
                            break;
                        }
                    }

                    bos.close();
                    fos.close();

                    // System.out.println("File received: " + fileName);
                    messages.append("File received: " + fileName + "\n");

                } else {
                    messages.append(message + "\n");
                }
            }

        } catch (Exception e) {
            System.out.println("Error: " + e);
            privateChat.dispose();
            JOptionPane.showMessageDialog(null, "Other client disconnected", "Error", JOptionPane.ERROR_MESSAGE);
            
        }

    }

    public serverp2p(String user, Socket socketInv, String host) {

        System.out.println("serverp2p started");

        if (socketInv != null) {
            try {
                Thread serverp2pThread = new Thread() {
                    public void run() {
                        try {
                            serverp2pThread(socketInv, user, host);
                        } catch (Exception e) {
                            System.out.println("Error: " + e);
                        }
                    }
                };

                serverp2pThread.start();

            } catch (Exception e) {
                System.out.println("Error: " + e);
            }

        } else {

            try {
                ServerSocket serverSocket = new ServerSocket(0);
                port = serverSocket.getLocalPort();
                System.out.println("Puerto: " + port);
                System.out.println("port printed");

                Thread whileLoop = new Thread() {
                    public void run() {
                        try {
                            while (true) {

                                Socket socket = serverSocket.accept();
                                System.out.println("Conectado");

                                Thread serverp2pThread = new Thread() {
                                    public void run() {
                                        try {
                                            serverp2pThread(socket, user, host);
                                        } catch (Exception e) {
                                            System.out.println("Error: " + e);
                                        }
                                    }
                                };

                                serverp2pThread.start();

                            }
                        } catch (Exception e) {
                            System.out.println("Error: " + e);
                        }
                    }
                };

                System.out.println("while loop started");
                whileLoop.start();

                // while (true) {
                // Socket socket = serverSocket.accept();
                // System.out.println("Conectado");

                // Thread serverp2pThread = new Thread() {
                // public void run() {
                // try {
                // serverp2pThread(socket, user);
                // } catch (Exception e) {
                // System.out.println("Error: " + e);
                // }
                // }
                // };

                // serverp2pThread.start();

                // }

            } catch (Exception e) {
                System.out.println("Error: " + e);
            }

        }

    }

}
