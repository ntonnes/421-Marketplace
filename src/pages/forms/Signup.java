package pages.forms;
import database.Database;
import main.Main;
import pages.Page;
import pages.Utils;

import java.text.*;
import java.util.Date;
import javax.swing.*;

public class Signup extends Form {

    private static JTextField firstNameField = new JTextField(20);
    private static JTextField lastNameField = new JTextField(20);
    private static JTextField emailField = new JTextField(20);
    private static JPasswordField passwordField = new JPasswordField(20);
    private static JFormattedTextField dobField = addDateField();

    public Signup() {
        super("Create an Account");
    }

    @Override
    protected void populateContent() {

        // Add the components to the panel
        addLabel("First Name:", true);
        addTextField(firstNameField);
        addLabel("Last Name:", true);
        addTextField(lastNameField);
        addLabel("Email:", true);
        addTextField(emailField);
        addLabel("Password:", true);
        addTextField(passwordField);
        addLabel("Date of Birth (mm/dd/yyyy):", false);
        addTextField(dobField);
        addButton("Sign Up", BUTTON_GREEN, e -> submit());
    }


    @Override
    protected void submit() {

        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
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
                    Utils.showErr("Date of Birth cannot be in the future.");
                    return;
                }
            }
        } catch (ParseException e) {
            Utils.showErr("Invalid Date of Birth.");
            return;
        }

        // Validate non-null fields
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Utils.showErr("All fields must be filled out.");
            return;
        }

        // Check if email already exists
        if (Database.emailIsUnique(email)) {
            Database.createCustomer(Main.user, dob, password, email, firstName + " " + lastName);
            Page.goBack();
            return;
        } else {
            Utils.showErr("An account with that email already exists.");
            return;
        }
    }
}
