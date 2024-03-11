package pages;

import java.awt.*;
import java.awt.event.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.*;

import main.Main;
import users.*;

public class Login extends Page {
    private static JTextField emailField;
    private static JPasswordField passwordField;

    public Login() {
        super("Login", new BorderLayout());
        content.setLayout(new BoxLayout(content, BoxLayout.PAGE_AXIS));
    }

    @Override
    protected void populateContent() {
        // Add text entry fields for email and password
        emailField = new JTextField(20);
        passwordField = new JPasswordField(20);

        // Add an 'Log in' button that submits the typed information to validateLogin when clicked
        JButton loginButton = new JButton("Log in");
        loginButton.addActionListener(e -> validateLogin());

        // Create a label with a clickable "Create one" text that takes the user to the signup panel
        JLabel signupLabel = new JLabel("<html><body>Don't have an account? <a href='' style='color: #ADD8E6;'>Create one</a>.</body></html>");
        signupLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        signupLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { Page.goPage(new Signup()); }
        });

        // Add the components to the panel
        content.add(Utils.createLabel("Email:", Utils.arialB, false));
        content.add(emailField);
        content.add(Utils.createLabel("Password:", Utils.arialB, false));
        content.add(passwordField);
        content.add(Utils.createButton("Log In", e -> validateLogin(), Utils.arialB));
        content.add(signupLabel);
    }

    private static void validateLogin() {
        // Get the typed information
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());

        try {
            String sql = "SELECT * FROM Customer WHERE email = ?";
            PreparedStatement statement = database.Database.db.prepareStatement(sql);
            statement.setString(1, email);

            ResultSet resultSet = statement.executeQuery();

            // If a matching user was found
            if (resultSet.next()) {
                // If the password matches
                if (resultSet.getString("password").equals(password)) {
                    // Set the user in the Menu class and switch to the main menu
                    Main.setUser(new Customer(
                        resultSet.getInt("userID"), 
                        resultSet.getString("name"), 
                        email, 
                        password, 
                        resultSet.getString("dob")
                    )); 
                    goBack();
                    
                } else {
                    // If the password does not match
                    Utils.showErr("Password is incorrect.");
                }
            } else {
                Utils.showErr("An account with that email does not exist.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Utils.showErr("An error occurred while establishing a connection to the database.");
        }
    }
}