import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class ChatGUI extends JFrame  {


    private JTextArea messages; // Make messages an instance field
    private JPanel input;
    private JTextField tf;



    public ChatGUI() {

        
        

        // Frame
        JFrame frame = new JFrame("Chat Frame");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 700);

        // Panel for messages
        JPanel panel = new JPanel();
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

        // Input area
        input = new JPanel();
        input.setBounds(20, 580, 600, 50);
        input.setBackground(new Color(140, 140, 240));
        frame.add(input);

        // Input text
        JLabel inputLabel = new JLabel("Input: ");
        tf = new JTextField(10);
        tf.addActionListener(e -> {

            messages.append(tf.getText() + "\n");
            tf.setText("");
        });
        
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

    public String getInput() {
        return tf.getText();
    }

}
