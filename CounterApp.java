import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CounterApp extends JFrame {
    private int counterValue = 1;
    private JLabel counterLabel;

    public CounterApp() {
        // Set the title of the application window
        setTitle("Counter Application");

        // Set the default close operation
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create a panel to hold the components
        JPanel panel = new JPanel();

        // Create a label to display the counter value
        counterLabel = new JLabel("Counter: " + counterValue);
        panel.add(counterLabel);

        // Create a button to increment the counter
        JButton incrementButton = new JButton("Increment");
        incrementButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Multiply the counter value by 2
                counterValue *= 2;
                // Update the label text
                counterLabel.setText("Counter: " + counterValue);
            }
        });
        panel.add(incrementButton);

        // Add the panel to the frame
        add(panel);

        // Set the size of the window
        setSize(300, 100);

        // Center the window on the screen
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CounterApp app = new CounterApp();
            app.setVisible(true);
        });
    }
}
