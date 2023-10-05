import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 700);

        // Panel for messages
        panel = new JPanel();
        panel.setBounds(20, 20, 500, 100);
        frame.add(panel);

        // Mesagges area
        messages = new JTextArea();
        messages.setBounds(20, 20, 500, 100);
        messages.setEditable(false);
        panel.add(messages);

        

        // Format for messages
        JLabel format = new JLabel();
        format.setBounds(20, 550, 600, 100);
        frame.add(format);

        // Connected users area
        users = new JPanel();
        users.setBounds(640, 20, 40, 500);
        users.setBackground(new Color(140, 140, 240));
        frame.add(users);

        // Button to close application
        JButton close = new JButton();
        close.setBounds(640, 550, 40, 20);
        close.setText("Disconnect");
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
        input.setBounds(20, 580, 600, 80);
        input.setBackground(new Color(140, 140, 240));
        frame.add(input);

        // Input text
        JLabel inputLabel = new JLabel("Message: ");
        
        input.add(inputLabel);
        input.add(tf);

        input.add(close);

        // Adding components to the frame
        frame.getContentPane().add(BorderLayout.EAST, users);
        frame.getContentPane().add(BorderLayout.WEST, panel);
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
