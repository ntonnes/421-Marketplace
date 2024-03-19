package database;
import java.sql.*;

import main.Main;

public class User {
    protected int userID;
    
    // Constructor for a new guest user
    public User() {
        this.userID = createGuest();
    }

    // Constructor for an existing guest; only use for debugging
    public User(int userID) {
        this.userID = userID;
        getGuest(userID);
    }

    private int createGuest() {
        try (Connection conn = Database.connect()) {
            while (true) {
                int userID = (int) ((Math.random() * 9 + 1) * 100000000);  // Generate a 9-digit integer

                try (PreparedStatement checkStmt = conn.prepareStatement("SELECT userID FROM User WHERE userID = ?")) {
                    checkStmt.setInt(1, userID);

                    try (ResultSet rs = checkStmt.executeQuery()) {
                        if (!rs.next() && userID != Main.DEBUG_USERID) {  // If the userID doesn't exist in the database
                            try (PreparedStatement insertStmt = conn.prepareStatement("INSERT INTO User (userID) VALUES (?)")) {
                                insertStmt.setInt(1, userID);
                                insertStmt.executeUpdate();
                                System.out.println("\nNew guest user added to the database:\nUserID = " + userID + "}\n");
                            }
                            return userID;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error while accessing the database:");
            e.printStackTrace();
            return -1;
        }
    }

    // Only use for debugging
    private void getGuest(int userID) {
        try (Connection conn = Database.connect();
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO User (userID) VALUES (?)")) {
            stmt.setInt(1, userID);
            stmt.executeUpdate();
            System.out.println("\nNew guest user added to the database:\nUserID = " + userID + "}\n");
        } catch (SQLException e) {
            System.out.println("\nThe application closed unexpectedly in the last debug session.\n"+
                               "User{userID=\" + userID + \"} already exists in the database.\n" +
                               "\nSetting the current user to User{userID=" + userID + "}.\n");
        }
    }

    // Removes a guest user from the database, used at the end of a session or when a user logs in
    public void deleteGuest() {
        int userID = Main.user.getUserID();
        try (Connection conn = DriverManager.getConnection(Database.DB_URL, Database.USER, Database.PASS)) {
            // Delete from InCart table
            try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM InCart WHERE userID = ?")) {
                stmt.setInt(1, userID);
                stmt.executeUpdate();
                System.out.println("All items removed from the cart of " + userID + ".");
            }

            // Delete from User table
            try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM User WHERE userID = ?")) {
                stmt.setInt(1, userID);
                stmt.executeUpdate();
                System.out.println("User " + userID + " deleted from the User table");
            }
        } catch (SQLException e) {
            System.out.println("Error while deleting user from the database");
            e.printStackTrace();
        }
    }

    public int getUserID() {
        return userID;
    }
}