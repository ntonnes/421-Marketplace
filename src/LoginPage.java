import java.sql.*;

import database.Database;
import database.users.*;


public class LoginPage implements Page {

    LoginPage() {
    }

    @Override
    public void display(User user) {
        System.out.println("Login Page:");
        System.out.println("\t(1) Login\n\t(2) Sign Up\n\t(3) Continue as Guest");
    }

    @Override
    public void go(int option) {
        switch (option) {
            case 1:

                App.scanner.nextLine();
                System.out.println("Enter your email: ");
                String email = App.scanner.nextLine();
                System.out.println("Enter your password: ");
                String password = App.scanner.nextLine();
                if (submit(email, password, App.getUser()) == 0) {
                    App.pop();
                }
                break;

            case 2:
                App.push(new SignupPage());
                break;
            case 3:
                App.goHome();
                break;
            default:
                System.out.println("Invalid option. Try again.");
        }
    }

    private int submit(String email, String password, User user) {

        try (Connection conn = Database.connect();
        PreparedStatement getstmt = conn.prepareStatement("SELECT * FROM Customer WHERE email = ?");
        PreparedStatement deleteStmt = conn.prepareStatement("DELETE FROM User WHERE userID = ?")) {
            getstmt.setString(1, email);

            ResultSet resultSet = getstmt.executeQuery();

            if (resultSet.next()) {
                // If the (email, password) pair exists in the db
                if (resultSet.getString("password").equals(password)) {

                    // Delete the temporary guest
                    deleteStmt.setInt(1, App.getUser().getUserID());
                    deleteStmt.executeUpdate();
                    System.out.println("Deleted temporary guest with userID: " + user.getUserID());

                    // Set the user in the Menu class and switch to the main menu
                    Customer customer = new Customer(
                            resultSet.getInt("userID"),
                            resultSet.getString("name"),
                            email,
                            password,
                            resultSet.getString("dob")
                    );
                    System.out.println("\nSuccessfully logged in as user " + customer.getName() + ":\n" + customer.toString() + "\n");
                    App.login(customer);
                    return 0;


                } else {
                    System.out.println("Incorrect password. Try again.");
                    return -1;
                }
            } else {
                System.out.println("An account with that email does not exist.");
                return -1;
            }
        } catch (SQLException e) {
            System.out.println("An error occurred while executing an SQL statement:" + e.getMessage());
            return -1;
        }
    }
}