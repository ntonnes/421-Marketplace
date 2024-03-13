package pages.forms;

import java.awt.GridBagConstraints;
import java.sql.*;

import javax.swing.*;

import database.Database;
import database.users.*;
import main.Main;
import pages.Menu;
import pages.Page;
import pages.UIUtils;

public class Login extends Page {
    private static JTextField emailField = new JTextField(20);
    private static JPasswordField passwordField = new JPasswordField(20);

    public Login() {
        super(new Menu(), "Login");
    }

    // Fills the GUI with page content
    @Override
    protected void populateContent() {

        // Add components to the panel
        JPanel emailEntry = UIUtils.createFieldPanel("Email:", false, emailField);
        JPanel passwordEntry = UIUtils.createFieldPanel("Password:", false, passwordField);
        JLabel signupLink = UIUtils.createHyperlink("Don't have an account? ", "Create one", ".", () -> Main.goPage(new Signup()));
        JButton loginButton = UIUtils.createButton("Log in", e -> submit());

        UIUtils.addToGrid(content, emailEntry, UIUtils.createGBC(1, 0, 1, 0, GridBagConstraints.HORIZONTAL));
        UIUtils.addToGrid(content, passwordEntry, UIUtils.createGBC(1, 1, 1, 0, GridBagConstraints.HORIZONTAL));
        UIUtils.addToGrid(content, signupLink, UIUtils.createGBC(1, 2, 1, 0, GridBagConstraints.HORIZONTAL));
        UIUtils.addToGrid(content, loginButton, UIUtils.createGBC(1, 3, 1, 0, GridBagConstraints.HORIZONTAL));

    }

    // Performs queries to validate the user's input
    private void submit() {
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
                    Main.goBack();

                } else {
                    UIUtils.showErr("Password is incorrect.");
                }
            } else {
                UIUtils.showErr("An account with that email does not exist.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            UIUtils.showErr("An error occurred while executing an SQL statement.");
        }
    }

}