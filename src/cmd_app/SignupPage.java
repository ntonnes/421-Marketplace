package cmd_app;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import database.Customer;
import database.Database;
import database.User;

public class SignupPage implements Page {

    SignupPage() {
    }

    @Override
    public void display(User user) {
        System.out.println("Sign Up Page:");
        System.out.println("\t(1) Sign Up");
        System.out.println("\t(2) Go Back");
    }

    @Override
    public void go(int option) {
        switch (option) {
            case 1:
                App.scanner.nextLine();
                System.out.println("Enter your name: ");
                String name = App.scanner.nextLine();
                System.out.println("Enter your email: ");
                String email = App.scanner.nextLine();
                System.out.println("Enter your password: ");
                String password = App.scanner.nextLine();
                System.out.println("Enter your date of birth (mm/dd/yyy) or press enter to skip: "); 
                String dob = App.scanner.nextLine();
                int userID = App.getUser().getUserID();
                if (submit(userID, name, email, password, dob) == 0) {
                    Customer customer = new Customer(userID, name, email, password, dob);
                    App.login(customer);
                    App.pop();
                }
            case 2:
            default:    
                System.out.println("Invalid option. Try again.");
        }
    }

    private int submit(int userID, String name, String email, String password, String dob) {

        // Validate DOB
        if (dob !=null) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("mm/dd/yyyy");
                Date date = sdf.parse(dob);
                dob = sdf.format(date);
            } catch (ParseException e) {
                System.out.println("Invalid date format. Please use mm/dd/yyyy.");
                return -1;
            }
        }

        // Validate non-null fields
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            System.out.println("All fields must be filled out.");
            return -1;
        }

        try (Connection conn = DriverManager.getConnection(Database.DB_URL, Database.USER, Database.PASS);
        PreparedStatement getStmt = conn.prepareStatement("SELECT * FROM Customer WHERE email = ?");
        PreparedStatement insertStmt = conn.prepareStatement("INSERT INTO Customer (userID, Name, Email, Password, DOB) VALUES (?, ?, ?, ?, ?)")) {
            
            getStmt.setString(1, email);
            ResultSet resultSet = getStmt.executeQuery();

            // Check if email already exists
            if (resultSet.next()) {
                System.out.println("An account with that email already exists.");
                return -1;
            
            // If email is unique, create the customer
            } else {
                insertStmt.setInt(1, userID);
                insertStmt.setString(2, dob);
                insertStmt.setString(3, password);
                insertStmt.setString(4, email);
                insertStmt.setString(5, name);
                insertStmt.executeUpdate();

                System.out.println("\nSuccessfully registered user " + name + ".\n");
                return 0;
            }
        } catch (SQLException e) {
            System.out.println("An error occurred while executing an SQL statement:" + e.getMessage());
            return -1;
        }
    }
}
