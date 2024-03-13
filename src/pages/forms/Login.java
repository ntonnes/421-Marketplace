package pages.forms;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;

import javax.swing.*;

import database.Database;
import database.users.*;
import main.Main;
import pages.Page;
import pages.Utils;

public class Login extends Form {
    private static JTextField emailField = new JTextField(20);
    private static JPasswordField passwordField = new JPasswordField(20);

    public Login() {
        super("Login");
    }

    // Fills the GUI with page content
    @Override
    protected void populateContent() {

        // Create a custom label with a clickable "Create one" text that takes the user to the signup panel
        JLabel signupLabel = new JLabel("<html><body>Don't have an account? <a href='' style='color: #ADD8E6;'>Create one</a>.</body></html>");
        signupLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        signupLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { Page.goPage(new Signup()); }
        });

        // Add the components to the panel
        addLabel("Email:", false);
        addTextField(emailField);
        addLabel("Password:", false);
        addTextField(passwordField);
        addButton("Log in", BUTTON_BLUE, e -> submit());
        content.add(signupLabel, gbcL);
    }

    // Performs queries to validate the user's input
    @Override
    protected void submit() {
        // Get the typed information
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());

        try (Connection conn = DriverManager.getConnection(Database.DB_URL, Database.USER, Database.PASS);
        PreparedStatement getstmt = conn.prepareStatement("SELECT * FROM Customer WHERE email = ?");
        PreparedStatement deleteStmt = conn.prepareStatement("DELETE FROM User WHERE userID = ?")) {
            getstmt.setString(1, email);

            ResultSet resultSet = getstmt.executeQuery();

            if (resultSet.next()) {
                // If the (email, password) pair exists in the db
                if (resultSet.getString("password").equals(password)) {

                    // Delete the temporary guest
                    deleteStmt.setInt(1, Main.user.getUserID());
                    deleteStmt.executeUpdate();
                    System.out.println("Deleted temporary guest with userID: " + Main.user.getUserID());

                    // Set the user in the Menu class and switch to the main menu
                    Main.setUser(new Customer(
                            resultSet.getInt("userID"),
                            resultSet.getString("name"),
                            email,
                            password,
                            resultSet.getString("dob")
                    ));

                    // Go back to the main menu
                    Customer customer = (Customer) Main.user;
                    System.out.println("\nSuccessfully logged in as user " + customer.getName() + ":\n" + customer.toString() + "\n");
                    goBack();

                } else {
                    Utils.showErr("Password is incorrect.");
                }
            } else {
                Utils.showErr("An account with that email does not exist.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Utils.showErr("An error occurred while executing an SQL statement.");
        }
    }
}