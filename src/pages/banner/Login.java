package pages.banner;

import java.sql.*;
import javax.swing.*;

import database.Customer;
import database.Database;
import main.Main;
import pages.utils.ColumnPage;
import pages.utils.Popup;
import static pages.utils.UISettings.*;

public class Login extends ColumnPage {
    private static JTextField emailField = new JTextField(20);
    private static JPasswordField passwordField = new JPasswordField(20);
    private JButton loginButton;

    public Login() {
        super("Login");

        emailField.addActionListener(e -> passwordField.requestFocus());
        passwordField.addActionListener(e -> loginButton.doClick());
    }

    // Fills the GUI with page content
    @Override
    protected void populateContent() {

        // Add components to the panel
        JPanel emailEntry = createFieldPanel("Email:", false, emailField);
        JPanel passwordEntry = createFieldPanel("Password:", false, passwordField);
        JLabel signupLink = createHyperlink("Don't have an account? ", "Create one", ".", () -> Main.goNew(new Signup(), "SignUp"));
       loginButton = createButton("Log in", BUTTON_BLUE, e -> submit());

        addBuffer(0.05);
        addComponent(emailEntry, 0);
        addBuffer(0.02);
        addComponent(passwordEntry, 0);
        addBuffer(0.02);
        addComponent(loginButton, 0.05);
        addComponent(signupLink, 0);
        addBuffer(0.9);
        addSideBuffers();
    }

    // Performs queries to validate the user's input
    private void submit() {
        // Get the typed information
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());

        try (Connection conn = Database.connect();
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
                    Popup.showMsg("Successfully logged in as " + customer.getName() + "!");
                    Main.goBack();

                } else {
                    Popup.showErr("Password is incorrect.");
                }
            } else {
                Popup.showErr("An account with that email does not exist.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Popup.showErr("An error occurred while executing an SQL statement.");
        }
    }

}