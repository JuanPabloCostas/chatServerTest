import java.awt.BorderLayout;
import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.awt.event.*;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class ChatGUI extends JFrame  {

    private JFrame frame;
    public JTextArea messages; // Make messages an instance field
    private JPanel input;
    public JTextField tf = new JTextField(10);
    public JPanel users;
    private JPanel panel; 
    public JFrame requestFrame;
    public Socket socket;



    public ChatGUI(String host, Socket socket) {
        this.socket = socket;

        
        

        // Frame
        frame = new JFrame("Chat Frame" + " - " + host);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    try {
                        OutputStream outputStream = socket.getOutputStream();
                        byte[] msg = "END".getBytes();
                        outputStream.write(msg);
                        socket.close();
                        super.windowClosing(e);
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                        // Handle the exception appropriately
                    }
                    // Dispose the JFrame
                    frame.dispose();
                }
            });
        
        frame.setSize(700, 700);

        // Panel for messages
        panel = new JPanel();
        panel.setBounds(20, 20, 600, 500);
        panel.setBackground(new Color(140, 240, 240));
        frame.add(panel);

        // Mesagges area
        messages = new JTextArea();
        messages.setBounds(20, 20, 600, 500);
        messages.setBackground(new Color(140, 240, 240));
        messages.setEditable(false);
        panel.add(messages);

        

        // Format for messages
        JLabel format = new JLabel("Format: <user> <message>");
        format.setBounds(20, 550, 600, 20);
        format.setBackground(new Color(0, 140, 140));
        frame.add(format);


        // Dummy messages
        messages.append("Hello\n");
        messages.append("How are you?\n");
        messages.append("I'm fine\n");
        messages.append("Thanks\n");
        messages.append("Bye\n");

        // Connected users area
        users = new JPanel();
        users.setBounds(640, 20, 40, 500);
        users.setBackground(new Color(140, 140, 240));
        frame.add(users);

        // 323335

        // Button to close application
        JButton close = new JButton();
        close.setBounds(640, 550, 40, 20);
        close.setText("X");
        close.setBackground(new Color(140, 140, 240));
        close.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    OutputStream outputStream = socket.getOutputStream();
                    outputStream.write("END".getBytes());
                    outputStream.flush();
                    socket.close();
                } catch (Exception err) {
                    System.out.println("Error: " + err.getMessage());
                }
                
            }
        });

        


        



        

        // Input area
        input = new JPanel();
        input.setBounds(20, 580, 600, 50);
        input.setBackground(new Color(140, 140, 240));
        frame.add(input);

        // Input text
        JLabel inputLabel = new JLabel("Input: ");
        
        input.add(inputLabel);
        input.add(tf);

        input.add(close);

        // Adding components to the frame
        frame.getContentPane().add(BorderLayout.NORTH, panel);
        frame.getContentPane().add(BorderLayout.SOUTH, input);
        
        frame.setVisible(true);

    }

    public void addMessage(String msg) {
    SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
            messages.append(msg + "\n");
        }
    });}

    
    

}
