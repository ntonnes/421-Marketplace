import javax.swing.*;
import java.awt.*;

public class Menu {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Menu::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        UIManager.put("Panel.background", Color.DARK_GRAY);
        UIManager.put("Button.background", Color.LIGHT_GRAY);
        UIManager.put("Button.foreground", Color.BLACK);
        UIManager.put("Label.foreground", Color.WHITE);

        JFrame frame = new JFrame("421 Marketplace");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);

        JLabel title = new JLabel("421 Marketplace", SwingConstants.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, 30)); // Increase font size to 30
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0)); // Add 20 pixels of padding to the top

        JButton button1 = new JButton("Task 1");
        button1.addActionListener(e -> System.out.println("You selected task 1"));

        JButton button2 = new JButton("Task 2");
        button2.addActionListener(e -> System.out.println("You selected task 2"));

        JButton button3 = new JButton("Task 3");
        button3.addActionListener(e -> System.out.println("You selected task 3"));

        JButton button4 = new JButton("Quit");
        button4.addActionListener(e -> {
            System.out.println("Exiting the program...");
            System.exit(0);
        });

        JPanel buttonPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);

        buttonPanel.add(button1, gbc);
        buttonPanel.add(button2, gbc);
        buttonPanel.add(button3, gbc);
        buttonPanel.add(button4, gbc);

        frame.getContentPane().add(title, BorderLayout.NORTH);
        frame.getContentPane().add(buttonPanel, BorderLayout.CENTER);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}