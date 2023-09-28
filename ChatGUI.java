import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
    private JPanel users;
    private JPanel panel; 



    public ChatGUI() {

        
        

        // Frame
        frame = new JFrame("Chat Frame");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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

        // Connected users
        

        // Button for each user connected
        



        

        // Input area
        input = new JPanel();
        input.setBounds(20, 580, 600, 50);
        input.setBackground(new Color(140, 140, 240));
        frame.add(input);

        // Input text
        JLabel inputLabel = new JLabel("Input: ");
        
        input.add(inputLabel);
        input.add(tf);

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

    public void updateUsersList(String msg) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                System.out.println("run started");
                
                users.removeAll();
                frame.revalidate();
                users.revalidate();
                panel.revalidate();
                frame.revalidate();
                String[] userList = msg.split("#");
                System.out.println("array created");
                for (String user : userList) {
                    System.out.println(user + " start loop");
                    JButton userButton = new JButton(user);
                    // userButton.addActionListener(e -> {
                    //     messages.append(user + "\n");
                    // });

                    users.add(userButton);

                    
                    System.out.println(user + " added");
                }

                System.out.println("loop done");
                users.revalidate();
                
            }
        });
    }

    // send mesagges back to the client
    

}
