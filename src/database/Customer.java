package database;

import java.sql.*;

import main.Main;

public class Customer extends User {
    protected String name;
    protected String email;
    protected String password;
    protected String dob;

    public Customer(int userID, String name, String email, String password, String dob) {
        super(userID);
        this.name = name;
        this.email = email;
        this.password = password;
        this.dob = dob;
    }

    // Log out the current user
    public void logout() {
        Main.setUser(new User()); // Set the current user to a guest user
        System.out.println("Successfully logged out user " + name + ":\n" + this.toString() + "\n");
    }

    // Getter methods
    public String getName() {
        return name;
    }
    public String getEmail() {
        return email;
    }
    public String getPassword() {
        return password;
    }
    public String getDob() {
        return dob;
    }

    @Override
    public String toString() {
        return "\nCustomer:\n" +
            "    UserID: " + userID + '\n' +
            "    Name: " + name + '\n' +
            "    Email: " + email + '\n' +
            "    Password: " + password + '\n' +
            "    Date of Birth: " + dob + '\n';
    }


    // Setter methods
    public void setName(String name) {
        try (Connection conn = Database.connect();
        PreparedStatement stmt = conn.prepareStatement("UPDATE User SET name = ? WHERE userID = ?")) {
            stmt.setString(1, name);
            stmt.setInt(2, userID);
            stmt.executeUpdate();
            System.out.println("Name changed to " + name);
        } catch (SQLException e) {
            System.out.println("Error while updating user name");
            e.printStackTrace();
        }
    }

    public void setEmail(String email) {
        try (Connection conn = Database.connect();
        PreparedStatement stmt = conn.prepareStatement("UPDATE User SET email = ? WHERE userID = ?")) {
            stmt.setString(1, email);
            stmt.setInt(2, userID);
            stmt.executeUpdate();
            System.out.println("Email changed to " + email);
        } catch (SQLException e) {
            System.out.println("Error while updating user email");
            e.printStackTrace();
        }
    }

    public void setPassword(String password) {
        try (Connection conn = Database.connect();
        PreparedStatement stmt = conn.prepareStatement("UPDATE User SET password = ? WHERE userID = ?")) {
            stmt.setString(1, password);
            stmt.setInt(2, userID);
            stmt.executeUpdate();
            System.out.println("Password changed to " + password);
        } catch (SQLException e) {
            System.out.println("Error while updating user password");
            e.printStackTrace();
        }
    }

    public void setDob(String dob) {
        try (Connection conn = Database.connect();
        PreparedStatement stmt = conn.prepareStatement("UPDATE User SET dob = ? WHERE userID = ?")) {
            stmt.setString(1, dob);
            stmt.setInt(2, userID);
            stmt.executeUpdate();
            System.out.println("Date of birth changed to " + dob);
        } catch (SQLException e) {
            System.out.println("Error while updating user date of birth");
            e.printStackTrace();
        }
    }
}