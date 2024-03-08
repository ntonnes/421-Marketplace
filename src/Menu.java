import javax.swing.*;
import java.awt.*;

public class Menu {
    private static JFrame frame;
    private static JPanel buttonPanel;
    private static JPanel loginPanel;
    private static JLabel nameLabel;
    private static JTextField emailField;
    private static JPasswordField passwordField;

    // This class contains the name, email, and userID of the user who is logged into the app. user=NULL until
    // the user completes the login task, after which the user instance becomes populated with that user's info.
    // Use this information to execute tasks and queries specific to the user.
    // TODO: force the user to either log in, or continue as guest. If the user is a guest, generate a new userID
    // TODO: and add it to the database. Delete the user from the database when the application is closed.
    private static Login.User user;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Menu::createAndShowGUI);
    }

    // This is all for the GUI
    private static void createAndShowGUI() {
        UIManager.put("Panel.background", Color.DARK_GRAY);
        UIManager.put("Button.background", Color.LIGHT_GRAY);
        UIManager.put("Button.foreground", Color.BLACK);
        UIManager.put("Label.foreground", Color.WHITE);

        // Initializes the application managed by the OS
        frame = new JFrame("421 Marketplace");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);

        // Places the large title in the top + center of the window
        JLabel title = new JLabel("421 Marketplace", SwingConstants.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, 30));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        // Places a welcome message for the user beneath the title
        nameLabel = new JLabel();
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setHorizontalAlignment(JLabel.CENTER);
        nameLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        // Groups the title and welcome message in a panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(title, BorderLayout.NORTH);
        topPanel.add(nameLabel, BorderLayout.CENTER);
        topPanel.setBackground(Color.DARK_GRAY);

        // Sets the vertical positions of the banner panel (title+welcome message) and the button panel.
        buttonPanel = new JPanel(new GridBagLayout());
        frame.getContentPane().add(topPanel, BorderLayout.NORTH);
        frame.getContentPane().add(buttonPanel, BorderLayout.CENTER);

        // Sets the horizontal positions of the banner panel (title+welcome message) and the button panel.
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);

        // Initialize buttons to execute tasks
        // TODO: Implement Task 1
        JButton button1 = new JButton("Task 1");
        button1.addActionListener(e -> System.out.println("You selected task 1"));

        // TODO: Implement Task 2
        JButton button2 = new JButton("Task 2");
        button2.addActionListener(e -> System.out.println("You selected task 2"));

        // TODO: Implement Task 3
        JButton button3 = new JButton("Task 3");
        button3.addActionListener(e -> System.out.println("You selected task 3"));

        // Exits the program and prints a console message
        // TODO: Handle the logged-in user state
        JButton button4 = new JButton("Quit");
        button4.addActionListener(e -> {
            System.out.println("Exiting the program...");
            System.exit(0);
        });

        // Calls the method to complete the login task when selected
        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> showLoginPanel());

        // Adds the buttons to the GUI
        buttonPanel.add(button1, gbc);
        buttonPanel.add(button2, gbc);
        buttonPanel.add(button3, gbc);
        buttonPanel.add(button4, gbc);
        buttonPanel.add(loginButton, gbc);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // Method to show the login panel in the GUI
    private static void showLoginPanel() {
        // Initialize the panel
        loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setBackground(Color.DARK_GRAY);

        // Initialize a grid for formatting within the panel
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);

        // Add a boxes to enter email and password
        emailField = new JTextField(20);
        passwordField = new JPasswordField(20);

        // Add an 'Enter' button that submits the typed information to validateAndLogin when clicked
        JButton enterButton = new JButton("Enter");
        enterButton.addActionListener(e -> validateAndLogin());

        // Place the initialized elements in the panel grid
        loginPanel.add(new JLabel("Email:"), gbc);
        loginPanel.add(emailField, gbc);
        loginPanel.add(new JLabel("Password:"), gbc);
        loginPanel.add(passwordField, gbc);
        loginPanel.add(enterButton, gbc);

        // Remove the tasks button panel and add the newly created login panel
        frame.getContentPane().remove(buttonPanel);
        frame.getContentPane().add(loginPanel, BorderLayout.CENTER);
        frame.revalidate();
        frame.repaint();
    }

    // Method to adjust the GUI according to the results of the login attempt
    private static void validateAndLogin() {
        // Get the typed information
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());

        // Get the user object if the login is valid
        user = Login.validate(email, password);

        // If the login is valid, return to the original home panel and show the welcome message
        if (user != null) {
            nameLabel.setText("Hello, " + user.name + "!");

            frame.getContentPane().remove(loginPanel);
            frame.getContentPane().add(buttonPanel, BorderLayout.CENTER);

            frame.revalidate();
            frame.repaint();

        // If the login is invalid, show an error popup
        } else {
            JOptionPane.showMessageDialog(frame, "Invalid email or password.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}