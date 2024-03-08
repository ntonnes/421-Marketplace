import javax.swing.*;
import java.awt.*;

public class Menu {
    private static JFrame frame;
    private static JPanel buttonPanel;
    private static JPanel loginPanel;
    private static JLabel nameLabel;
    private static JTextField emailField;
    private static JPasswordField passwordField;
    private static Login.User user; // Store the logged-in user

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Menu::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        UIManager.put("Panel.background", Color.DARK_GRAY);
        UIManager.put("Button.background", Color.LIGHT_GRAY);
        UIManager.put("Button.foreground", Color.BLACK);
        UIManager.put("Label.foreground", Color.WHITE);

        frame = new JFrame("421 Marketplace");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);

        JLabel title = new JLabel("421 Marketplace", SwingConstants.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, 30));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        nameLabel = new JLabel();
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setHorizontalAlignment(JLabel.RIGHT);
        nameLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(title, BorderLayout.CENTER);
        topPanel.add(nameLabel, BorderLayout.EAST);
        topPanel.setBackground(Color.DARK_GRAY);

        buttonPanel = new JPanel(new GridBagLayout());
        frame.getContentPane().add(topPanel, BorderLayout.NORTH);
        frame.getContentPane().add(buttonPanel, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);

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

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> showLoginPanel());

        buttonPanel.add(button1, gbc);
        buttonPanel.add(button2, gbc);
        buttonPanel.add(button3, gbc);
        buttonPanel.add(button4, gbc);
        buttonPanel.add(loginButton, gbc);

        // Now it's safe to add buttonPanel to the frame
        frame.getContentPane().add(topPanel, BorderLayout.NORTH);
        frame.getContentPane().add(buttonPanel, BorderLayout.CENTER);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static void showLoginPanel() {
        loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setBackground(Color.DARK_GRAY);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);

        emailField = new JTextField(20);
        passwordField = new JPasswordField(20);

        JButton enterButton = new JButton("Enter");
        enterButton.addActionListener(e -> validateAndLogin());

        loginPanel.add(new JLabel("Email:"), gbc);
        loginPanel.add(emailField, gbc);
        loginPanel.add(new JLabel("Password:"), gbc);
        loginPanel.add(passwordField, gbc);
        loginPanel.add(enterButton, gbc);

        frame.getContentPane().remove(buttonPanel);
        frame.getContentPane().add(loginPanel, BorderLayout.CENTER);

        frame.revalidate();
        frame.repaint();
    }

    private static void validateAndLogin() {
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());

        user = Login.validate(email, password); // validate now returns a User object

        if (user != null) { // Check if user is not null instead of checking if validate returned true
            nameLabel.setText("Hello, " + user.name + "!"); // Get name from User object

            frame.getContentPane().remove(loginPanel);
            frame.getContentPane().add(buttonPanel, BorderLayout.CENTER);

            frame.revalidate();
            frame.repaint();
        } else {
            JOptionPane.showMessageDialog(frame, "Invalid email or password.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}