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
            privateChat = new JFrame("Private Chat with " + user);


            privateChat.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

            privateChat.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    try {
                        OutputStream outputStream = socket.getOutputStream();
                        byte[] msg = "END".getBytes();
                        outputStream.write(msg);
                        socket.close();
                        privateChat.dispose();
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                        // Handle the exception appropriately
                    }

                    // Dispose the JFrame
                    privateChat.dispose();
                }
            });





            
            privateChat.setSize(700, 700);

            JPanel panel = new JPanel();
            panel.setBounds(20, 20, 600, 500);
            panel.setBackground(new Color(140, 240, 240));

            JTextArea messages = new JTextArea();
            messages.setBounds(20, 20, 600, 500);
            messages.setBackground(new Color(140, 240, 240));
            messages.setEditable(false);
            panel.add(messages);

            
            // Send message
            JPanel input = new JPanel();
            input.setBounds(20, 550, 600, 50);
            input.setBackground(new Color(140, 240, 240));

            JTextField tf = new JTextField(10);
            tf.setBounds(20, 550, 600, 50);
            tf.setBackground(new Color(140, 240, 240));

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

            privateChat.add(panel);

            privateChat.add(input);

            privateChat.setLayout(null);
            privateChat.setVisible(true);

            // choose file
            JPanel chooseFile = new JPanel();
            chooseFile.setBounds(20, 600, 600, 50);
            chooseFile.setBackground(new Color(140, 240, 240));

            JButton choose = new JButton("Choose file");
            choose.setBounds(20, 600, 600, 50);
            choose.setBackground(new Color(140, 240, 240));

            choose.addActionListener(e -> {
                Frame frame = new Frame(); // You can pass your main application frame here

                FileDialog fileDialog = new FileDialog(frame, "Select File", FileDialog.LOAD);
                fileDialog.setVisible(true);

                String directory = fileDialog.getDirectory();
                String file = fileDialog.getFile();

                if (directory != null && file != null) {
                    String filePath = directory + file;
                    System.out.println("Selected file: " + filePath);

                    // Send message of incoming file
                    try {
                        byte[] msg = ("PUTFILE " + file).getBytes();
                        outputStream.write(msg);

                        // Send file
                        File fileToSend = new File(filePath);
                        byte[] fileBytes = Files.readAllBytes(fileToSend.toPath());
                        outputStream.write(fileBytes);

                    } catch (Exception ex) {
                        System.out.println("Error: " + ex);
                    }

                }
                
            });

            chooseFile.add(choose);

            privateChat.add(chooseFile);

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
                }
                else if (message.startsWith("PUTFILE")) {
                    
                    //get file name
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




                }
                else {
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

        if (socketInv!=null) {
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
            
        }
        else{

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
                //     Socket socket = serverSocket.accept();
                //     System.out.println("Conectado");

                //     Thread serverp2pThread = new Thread() {
                //         public void run() {
                //             try {
                //                 serverp2pThread(socket, user);
                //             } catch (Exception e) {
                //                 System.out.println("Error: " + e);
                //             }
                //         }
                //     };

                //     serverp2pThread.start();
                    
                // }


            } catch (Exception e) {
                System.out.println("Error: " + e);
            }
    
        }

        
        
        
    }

    

    
}
