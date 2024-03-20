package pages.banner;

import main.Main;
import pages.utils.ColumnPage;
import pages.utils.Popup;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import javax.swing.*;

import database.Customer;
import database.Database;

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
        JPanel dobEntry = createFieldPanel("Date of Birth (yyyy/mm/dd):", false, dobField);
        signupButton = createButton("Sign Up", BUTTON_GREEN, e -> submit());

        addBuffer(0.05);
        addComponent(fNameEntry, 0);
        addBuffer(0.02);
        addComponent(lNameEntry, 0);
        addBuffer(0.02);
        addComponent(emailEntry, 0);
        addBuffer(0.02);
        addComponent(passwordEntry, 0);
        addBuffer(0.02);
        addComponent(dobEntry, 0);
        addBuffer(0.02);
        addComponent(signupButton, 0.1);
        addBuffer(0.9);
        addSideBuffers();
    }


    private void submit() {
        Integer userID = Main.user.getUserID();
        String name = firstNameField.getText() + " " + lastNameField.getText();
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());
        String dobString = dobField.getText();
        java.sql.Date sqlDate = null;

        // Validate DOB
        if (!dobString.isEmpty() && !dobString.equals("----/--/--")) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
                LocalDate dob = LocalDate.parse(dobString, formatter);

                // Check if the date is in the future
                if (dob.isAfter(LocalDate.now())) {
                    Popup.showErr("Date of Birth cannot be in the future.");
                    dobField.setText(""); // Reset the dobField
                    return;
                }

                // Convert LocalDate to java.sql.Date
                sqlDate = java.sql.Date.valueOf(dob);
            } catch (DateTimeParseException e) {
                Popup.showErr("Invalid Date format. Please use yyyy/mm/dd");
                dobField.setText(""); // Reset the dobField
                return;
            }
        } else if (!dobString.equals("----/--/--")) {
            Popup.showErr("Invalid Date of Birth.");
            dobField.setText(""); // Reset the dobField
            return;
        }

        // Validate non-null fields
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Popup.showErr("Please fill out all required fields.");
            return;
        }

        try (Connection conn = Database.connect();
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
                insertStmt.setString(2, name);
                insertStmt.setString(3, email);
                insertStmt.setString(4, password);
                insertStmt.setDate(5, sqlDate);
                insertStmt.executeUpdate();

                Main.setUser(new Customer(userID, name, email, password, dobString));
                System.out.println("\nSuccessfully registered user " + name + ":\n" + Main.user.toString() + "\n");
                Popup.showMsg("Account created successfully! Welcome, " + name + "!");
                Main.goBack();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Popup.showErr("An error occurred while executing an SQL statement: " + e.getMessage());
        }
    }

}
