package pages;
import database.Database;
import main.Main;

import java.awt.BorderLayout;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.BoxLayout;
import javax.swing.JFormattedTextField;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.text.MaskFormatter;

public class Signup extends Page {

    private static JTextField firstNameField;
    private static JTextField lastNameField;
    private static JTextField emailField;
    private static JPasswordField passwordField;
    private static JFormattedTextField dobField;

    public Signup() {
        super("Signup", new BorderLayout());
        content.setLayout(new BoxLayout(content, BoxLayout.PAGE_AXIS));
    }

    @Override
    protected void populateContent() {
        // Create a mask for the date of birth field
        MaskFormatter dateMask;
        try {
            dateMask = new MaskFormatter("##/##/####");
            dateMask.setPlaceholderCharacter('-');
        } catch (ParseException ex) {
            ex.printStackTrace();
            return;
        }

        // Add fields to enter first name, last name, email, password, and date of birth
        firstNameField = (JTextField) Utils.beautifyField(new JTextField(), Utils.arial);
        lastNameField = (JTextField) Utils.beautifyField(new JTextField(), Utils.arial);
        emailField = (JTextField) Utils.beautifyField(new JTextField(), Utils.arial);
        passwordField = (JPasswordField) Utils.beautifyField(new JPasswordField(), Utils.arial);
        dobField = (JFormattedTextField) Utils.beautifyField(new JFormattedTextField(dateMask), Utils.arial);

        // Place the initialized entry fields and labels in the panel grids
        content.add(Utils.createLabel("First Name:", Utils.arialB, true));
        content.add(firstNameField);
        content.add(Utils.createLabel("Last Name:", Utils.arialB, true));
        content.add(lastNameField);
        content.add(Utils.createLabel("Email:", Utils.arialB, true));
        content.add(emailField);
        content.add(Utils.createLabel("Password:", Utils.arialB, true));
        content.add(passwordField);
        content.add(Utils.createLabel("Date of Birth (mm/dd/yyyy):", Utils.arialB, false));
        content.add(dobField);
        content.add(Utils.createButton("Sign Up", e -> validateSignup()));
    }


    /**
     * void validateSignup()
     * <p>
     * Validates the user's signup information by checking if the email is unique and then adding the user to the database.
     * If the email is not unique, an error message is displayed. Called when the user clicks the 'Sign Up' button in the
     * signup panel.
     */
    private static void validateSignup() {

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
