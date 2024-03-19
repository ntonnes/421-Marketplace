import java.io.IOException;
import java.sql.*;

import database.Database;
import database.users.*;
import jline.console.ConsoleReader;
import main.Main;
import pages.utils.Popup;


public class Login{
    private ConsoleReader console;

    public Login() {
        try {
            this.console = new ConsoleReader();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void display() {
        while (true) {
            try {
                System.out.println("Enter your email:");
                String email = console.readLine();
                System.out.println("Enter your password:");
                String password = console.readLine('*');

                submit(email, password);
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
            }
        }
    }

    private void submit(String email, String password) {

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