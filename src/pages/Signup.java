package pages;
import database.Database;
import database.users.Customer;
import main.Main;

import java.awt.GridBagConstraints;
import java.sql.*;
import java.text.*;
import java.util.Date;
import javax.swing.*;

public class Signup extends Page {

    private static JTextField firstNameField = new JTextField(20);
    private static JTextField lastNameField = new JTextField(20);
    private static JTextField emailField = new JTextField(20);
    private static JPasswordField passwordField = new JPasswordField(20);
    private static JFormattedTextField dobField = UIUtils.createDateField();

    public Signup() {
        super(new Menu(), "Create an Account");
    }

    @Override
    protected void populateContent() {

        // Add the components to the panel
        JPanel fNameEntry = UIUtils.createFieldPanel("First Name:", true, firstNameField);
        JPanel lNameEntry = UIUtils.createFieldPanel("Last Name:", true, lastNameField);
        JPanel emailEntry = UIUtils.createFieldPanel("Email:", true, emailField);
        JPanel passwordEntry = UIUtils.createFieldPanel("Password:", true, passwordField);
        JPanel dobEntry = UIUtils.createFieldPanel("Date of Birth (mm/dd/yyyy):", false, dobField);
        JButton signupButton = UIUtils.createButton("Sign Up", e -> submit());

        UIUtils.addToGrid(content, fNameEntry, UIUtils.createGBC(0, 0, 1, 1, GridBagConstraints.HORIZONTAL));
        UIUtils.addToGrid(content, lNameEntry, UIUtils.createGBC(0, 1, 1, 1, GridBagConstraints.HORIZONTAL));
        UIUtils.addToGrid(content, emailEntry, UIUtils.createGBC(0, 2, 1, 1, GridBagConstraints.HORIZONTAL));
        UIUtils.addToGrid(content, passwordEntry, UIUtils.createGBC(0, 3, 1, 1, GridBagConstraints.HORIZONTAL));
        UIUtils.addToGrid(content, dobEntry, UIUtils.createGBC(0, 4, 1, 1, GridBagConstraints.HORIZONTAL));
        UIUtils.addToGrid(content, signupButton, UIUtils.createGBC(0, 5, 1, 1, GridBagConstraints.HORIZONTAL));
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
                    UIUtils.showErr("Date of Birth cannot be in the future.");
                    return;
                }
            }
        } catch (ParseException e) {
            UIUtils.showErr("Invalid Date of Birth.");
            return;
        }

        // Validate non-null fields
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            UIUtils.showErr("All fields must be filled out.");
            return;
        }

        try (Connection conn = DriverManager.getConnection(Database.DB_URL, Database.USER, Database.PASS);
        PreparedStatement getStmt = conn.prepareStatement("SELECT * FROM Customer WHERE email = ?");
        PreparedStatement insertStmt = conn.prepareStatement("INSERT INTO Customer (userID, Name, Email, Password, DOB) VALUES (?, ?, ?, ?, ?)")) {
            
            getStmt.setString(1, email);
            ResultSet resultSet = getStmt.executeQuery();

            // Check if email already exists
            if (resultSet.next()) {
                UIUtils.showErr("An account with that email already exists.");
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
            UIUtils.showErr("An error occurred while executing an SQL statement.");
            return;
        }
    }
}
