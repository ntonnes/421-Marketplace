package pages.banner;

import main.Main;
import pages.utils.ColumnPage;
import pages.utils.Popup;

import java.sql.*;
import java.text.*;
import java.util.Date;
import javax.swing.*;

import database.Database;
import database.users.Customer;
import static pages.utils.UISettings.*;


public class Signup extends ColumnPage {

    private static JTextField firstNameField = new JTextField(20);
    private static JTextField lastNameField = new JTextField(20);
    private static JTextField emailField = new JTextField(20);
    private static JPasswordField passwordField = new JPasswordField(20);
    private static JFormattedTextField dobField = createDateField();
    private JButton signupButton;

    public Signup() {
        super("Create an Account");

        firstNameField.addActionListener(e -> lastNameField.requestFocus());
        lastNameField.addActionListener(e -> emailField.requestFocus());
        emailField.addActionListener(e -> passwordField.requestFocus());
        passwordField.addActionListener(e -> dobField.requestFocus());
        dobField.addActionListener(e -> signupButton.doClick());
    }

    @Override
    protected void populateContent() {

        // Add the components to the panel
        JPanel fNameEntry = createFieldPanel("First Name:", true, firstNameField);
        JPanel lNameEntry = createFieldPanel("Last Name:", true, lastNameField);
        JPanel emailEntry = createFieldPanel("Email:", true, emailField);
        JPanel passwordEntry = createFieldPanel("Password:", true, passwordField);
        JPanel dobEntry = createFieldPanel("Date of Birth (mm/dd/yyyy):", false, dobField);
        signupButton = createButton("Sign Up", BUTTON_BLUE, e -> submit());

        addBuffer(0.05);
        addComponent(fNameEntry, 0.01);
        addBuffer(0.02);
        addComponent(lNameEntry, 0.01);
        addBuffer(0.02);
        addComponent(emailEntry, 0.01);
        addBuffer(0.02);
        addComponent(passwordEntry, 0.01);
        addBuffer(0.02);
        addComponent(dobEntry, 0.01);
        addBuffer(0.02);
        addComponent(signupButton, 0.1);
        addBuffer();
    }


    private void submit() {

        Integer userID = Main.user.getUserID();
        String name = firstNameField.getText() + " " + lastNameField.getText();
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());
        String dob = dobField.getText();


        // Validate DOB
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        sdf.setLenient(false);
        Date date = null;  // Initialize date as null
        try {
            if (!dob.isEmpty() && !dob.equals("--/--/----")) {
                date = sdf.parse(dob);
                // Check if the date is in the future
                if (date.after(new Date())) {
                    Popup.showErr("Date of Birth cannot be in the future.");
                    return;
                }
            }
        } catch (ParseException e) {
            Popup.showErr("Invalid Date of Birth.");
            return;
        }

        // Validate non-null fields
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Popup.showErr("All fields must be filled out.");
            return;
        }

        try (Connection conn = DriverManager.getConnection(Database.DB_URL, Database.USER, Database.PASS);
        PreparedStatement getStmt = conn.prepareStatement("SELECT * FROM Customer WHERE email = ?");
        PreparedStatement insertStmt = conn.prepareStatement("INSERT INTO Customer (userID, Name, Email, Password, DOB) VALUES (?, ?, ?, ?, ?)")) {
            
            getStmt.setString(1, email);
            ResultSet resultSet = getStmt.executeQuery();

            // Check if email already exists
            if (resultSet.next()) {
                Popup.showErr("An account with that email already exists.");
                return;
            
            // If email is unique, create the customer
            } else {
                insertStmt.setInt(1, userID);
                insertStmt.setString(2, dob);
                insertStmt.setString(3, password);
                insertStmt.setString(4, email);
                insertStmt.setString(5, name);
                insertStmt.executeUpdate();

                Main.setUser(new Customer(userID, name, email, password, dob));
                System.out.println("\nSuccessfully registered user " + name + ":\n" + Main.user.toString() + "\n");
                Main.goBack();
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Popup.showErr("An error occurred while executing an SQL statement.");
            return;
        }
    }

}
