package pages;
import database.Database;
import main.Main;
import java.awt.*;
import java.text.*;
import java.util.Date;
import javax.swing.*;
import javax.swing.text.MaskFormatter;

public class Signup extends Page {

    private static JTextField firstNameField;
    private static JTextField lastNameField;
    private static JTextField emailField;
    private static JPasswordField passwordField;
    private static JFormattedTextField dobField;

    public Signup() {
        super("Create an Account", new GridBagLayout());
    }

    @Override
    protected void populateContent() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Define the vertical space beneath each type of component
        GridBagConstraints gbcL = Utils.makeGBC(0, 0, 0, 0);
        GridBagConstraints gbcF = Utils.makeGBC(0, 0, 30, 0);
        GridBagConstraints gbcB = Utils.makeGBC(0, 0, 10, 0);
        gbcF.ipadx = 200;

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
        firstNameField = new JTextField(20);
        lastNameField = new JTextField(20);
        emailField = new JTextField(20);
        passwordField = new JPasswordField(20);
        dobField = new JFormattedTextField(dateMask);

        // Add an 'Sign Up' button that submits the typed information to validateSignup when clicked
        JButton signupButton = Utils.styleButton("Sign Up", new Color(0, 123, 255), 0, 30, e -> validateSignup());

        // Add the components to the panel
        content.add(Utils.createLabel("First Name:", Utils.arialB, true), gbcL);
        content.add(Utils.beautifyField(firstNameField, Utils.arial), gbcF);
        content.add(Utils.createLabel("Last Name:", Utils.arialB, true), gbcL);
        content.add(Utils.beautifyField(lastNameField, Utils.arial), gbcF);
        content.add(Utils.createLabel("Email:", Utils.arialB, true), gbcL);
        content.add(Utils.beautifyField(emailField, Utils.arial), gbcF);
        content.add(Utils.createLabel("Password:", Utils.arialB, true), gbcL);
        content.add(Utils.beautifyField(passwordField, Utils.arial), gbcF);
        content.add(Utils.createLabel("Date of Birth (mm/dd/yyyy):", Utils.arialB, false), gbcL);
        content.add(Utils.beautifyField(dobField, Utils.arial), gbcF);
        content.add(signupButton, gbcB);
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
