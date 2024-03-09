import java.sql.*;
import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.MatteBorder;

public class Login {
    // Database connection details
    private static final String DB_URL = "jdbc:db2://winter2024-comp421.cs.mcgill.ca:50000/COMP421";
    private static final String USER = "cs421g10";
    private static final String PASS = "Cat!Dog923";

    // GUI elements
    private static JFrame frame;
    private static JPanel buttonPanel;
    private static JPanel loginPanel;
    private static JPanel signupPanel;
    private static JTextField emailField;
    private static JPasswordField passwordField;
    private static JTextField firstNameField;
    private static JTextField lastNameField;
    private static JFormattedTextField dobField;


    // Class to hold name, userID, and email
    public static class User {
        public final String name;
        public final String userID;
        public final String email;
        public final String password;
        public final String dob;

        public User(String name, String userID, String email, String password, String dob) {
            this.name = name;
            this.userID = userID;
            this.email = email;
            this.password = password;
            this.dob = dob;
        }
    }
    public static User user = null;

    private static JTextComponent beautifyField(JTextComponent field) {
        field.setOpaque(false);
        field.setBorder(new MatteBorder(0, 0, 1, 0, Color.WHITE));
        field.setForeground(Color.WHITE);
        field.setFont(field.getFont().deriveFont(18f)); 
        field.setPreferredSize(new Dimension(field.getPreferredSize().width, 30)); 
        field.setCaretColor(Color.WHITE); 
        return field;
    }

    // Login/Sign Up Task
    // Method to show the login panel in the GUI
    public static void showLoginPanel() {
        // Get the frame and button panel from the Menu class
        frame = Menu.getFrame();
        buttonPanel = Menu.getButtons();

        // Initialize the panel
        loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setBackground(Color.DARK_GRAY);

        // Initialize a grid for formatting within the panel
        GridBagConstraints gbcLabel = new GridBagConstraints();
        gbcLabel.gridwidth = GridBagConstraints.REMAINDER;
        gbcLabel.fill = GridBagConstraints.HORIZONTAL;
        gbcLabel.insets = new Insets(20, 0, 0, 0); // smaller gap after label

        GridBagConstraints gbcField = new GridBagConstraints();
        gbcField.gridwidth = GridBagConstraints.REMAINDER;
        gbcField.fill = GridBagConstraints.HORIZONTAL;
        gbcField.insets = new Insets(1, 0, 0, 0); // larger gap after field

        // Add a boxes to enter email and password
        emailField = new JTextField(20);
        passwordField = new JPasswordField(20);

        // Add an 'Log in' button that submits the typed information to validateLogin when clicked
        JButton loginButton = new JButton("Log in");
        loginButton.addActionListener(e -> validateLogin());

        // Create a label with a clickable "Create one" text
        JLabel signupLabel = new JLabel("<html><body>Don't have an account? <a href='' style='color: #ADD8E6;'>Create one</a>.</body></html>");
        signupLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        signupLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                showSignupPanel();
            }
        });

        // Place the initialized elements in the panel grid
        loginPanel.add(new JLabel("Email:"), gbcLabel);
        loginPanel.add(emailField, gbcField);
        loginPanel.add(new JLabel("Password:"), gbcLabel);
        loginPanel.add(passwordField, gbcField);
        loginPanel.add(loginButton, gbcLabel);
        loginPanel.add(signupLabel, gbcField);

        // Remove the tasks button panel and add the newly created login panel
        frame.getContentPane().remove(buttonPanel);
        frame.getContentPane().add(loginPanel, BorderLayout.CENTER);
        frame.revalidate();
        frame.repaint();
    }

    private static void validateLogin() {
        // Get the typed information
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());

        try {
            Class.forName("com.ibm.db2.jcc.DB2Driver"); // Load the DB2 JDBC driver

            // Establish a connection with the database
            try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
                // Prepare SQL query to fetch user with matching email
                String sql = "SELECT * FROM Customer WHERE email = ?";
                PreparedStatement statement = conn.prepareStatement(sql);
                statement.setString(1, email);

                ResultSet resultSet = statement.executeQuery();

                // If a matching user was found
                if (resultSet.next()) {
                    // If the password matches
                    if (resultSet.getString("password").equals(password)) {
                        user = new User(
                            resultSet.getString("name"), 
                            resultSet.getString("userID"), 
                            email, 
                            password, 
                            resultSet.getString("dob")
                        );

                        Menu.setUser(user); 
                        frame.getContentPane().remove(loginPanel);
                        frame.getContentPane().add(buttonPanel, BorderLayout.CENTER);
                        frame.revalidate();
                        frame.repaint();
                        
                    } else {
                        // If the password does not match
                        JOptionPane.showMessageDialog(null, "Password is incorrect.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "An account with that email does not exist.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "An error occurred while establishing a connection to the database.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "An error occurred while loading the JDBC drivers.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Method to show the signup panel in the GUI
    private static void showSignupPanel() {
        // Initialize the panel
        JPanel signupPanel = new JPanel(new GridBagLayout());
        signupPanel.setBackground(Color.DARK_GRAY);

        // Initialize a grid for formatting within the panel
        GridBagConstraints gbcLabel = new GridBagConstraints();
        gbcLabel.gridwidth = GridBagConstraints.REMAINDER;
        gbcLabel.fill = GridBagConstraints.HORIZONTAL;
        gbcLabel.insets = new Insets(20, 0, 0, 0); // smaller gap after label

        GridBagConstraints gbcField = new GridBagConstraints();
        gbcField.gridwidth = GridBagConstraints.REMAINDER;
        gbcField.fill = GridBagConstraints.HORIZONTAL;
        gbcField.insets = new Insets(1, 0, 0, 0); // larger gap after field

        MaskFormatter dateMask;
        try {
            dateMask = new MaskFormatter("##/##/####");
            dateMask.setPlaceholderCharacter('_');
        } catch (ParseException ex) {
            ex.printStackTrace();
            return;
        }

        // Add boxes to enter first name, last name, email, password, and date of birth
        firstNameField = (JTextField) beautifyField(new JTextField(120));
        lastNameField = new JTextField(125);
        emailField = new JTextField(255);
        passwordField = new JPasswordField(255);
        dobField = new JFormattedTextField(dateMask);

        // Add a 'Sign Up' button that submits the typed information to the database when clicked
        JButton signupButton = new JButton("Sign Up");
        signupButton.addActionListener(e -> validateSignup());

        // Place the initialized elements in the panel grid
        signupPanel.add(new JLabel("<html><font color='red'>* </lfont>First Name:</html>"), gbcLabel);
        signupPanel.add(firstNameField, gbcField);
        signupPanel.add(new JLabel("<html><font color='red'>* </font>Last Name:</html>"), gbcLabel);
        signupPanel.add(lastNameField, gbcField);
        signupPanel.add(new JLabel("<html><font color='red'>* </font>Email:</html>"), gbcLabel);
        signupPanel.add(emailField, gbcField);
        signupPanel.add(new JLabel("<html><font color='red'>* </font>Password:</html>"), gbcLabel);
        signupPanel.add(passwordField, gbcField);
        signupPanel.add(new JLabel("Date of Birth (dd/mm/yyy):"), gbcLabel);
        signupPanel.add(dobField, gbcField);
        signupPanel.add(signupButton, gbcLabel);

        // Remove the login panel and add the newly created signup panel
        frame.getContentPane().remove(loginPanel);
        frame.getContentPane().add(signupPanel, BorderLayout.CENTER);
        frame.revalidate();
        frame.repaint();
    }

    private static void validateSignup() {

        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());
        String dob = dobField.getText();
        
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(null, "All fields must be filled out.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Class.forName("com.ibm.db2.jcc.DB2Driver"); // Load the DB2 JDBC driver

            // Establish a connection with the database
            try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
                // Check if email already exists
                String sql = "SELECT * FROM Customer WHERE email = ?";
                PreparedStatement statement = conn.prepareStatement(sql);
                statement.setString(1, email);
                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    JOptionPane.showMessageDialog(null, "An account with that email already exists.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Generate a random 9-digit userID
                String userID;
                do {
                    userID = String.format("%09d", new Random().nextInt(1000000000));

                    sql = "SELECT * FROM User WHERE userID = ?";
                    statement = conn.prepareStatement(sql);
                    statement.setString(1, userID);
                    resultSet = statement.executeQuery();
                } while (resultSet.next());

                // Insert new user into User table
                sql = "INSERT INTO User (userID) VALUES (?)";
                statement = conn.prepareStatement(sql);
                statement.setString(1, userID);
                statement.executeUpdate();

                // Insert new customer into Customer table
                sql = "INSERT INTO Customer (userID, DOB, Password, Email, Name) VALUES (?, ?, ?, ?, ?)";
                statement = conn.prepareStatement(sql);
                statement.setString(1, userID);
                statement.setString(2, dob);
                statement.setString(3, password);
                statement.setString(4, email);
                statement.setString(5, firstName + " " + lastName);
                statement.executeUpdate();

                // Return to the login screen
                frame.getContentPane().remove(signupPanel);
                frame.getContentPane().add(loginPanel, BorderLayout.CENTER);
                frame.revalidate();
                frame.repaint();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "An error occurred while establishing a connection to the database.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "An error occurred while loading the JDBC drivers.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
