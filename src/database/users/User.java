package database.users;
import java.sql.*;

import database.Database;

public class User {
    protected int userID;
    
    // Constructor for a new guest user
    public User() {
        this.userID = getUniqueUserID();
    }
    // Constructor for an existing user
    public User(int userID) {
        this.userID = userID;
    }

    private int getUniqueUserID() {
        try (Connection conn = DriverManager.getConnection(Database.DB_URL, Database.USER, Database.PASS)) {
            while (true) {
                int userID = (int) ((Math.random() * 9 + 1) * 100000000);  // Generate a 9-digit integer

                try (PreparedStatement checkStmt = conn.prepareStatement("SELECT userID FROM User WHERE userID = ?")) {
                    checkStmt.setInt(1, userID);

                    try (ResultSet rs = checkStmt.executeQuery()) {
                        if (!rs.next()) {  // If the userID doesn't exist in the database
                            try (PreparedStatement insertStmt = conn.prepareStatement("INSERT INTO User (userID) VALUES (?)")) {
                                insertStmt.setInt(1, userID);
                                insertStmt.executeUpdate();
                                System.out.println("New guest user added to the database:\nUser{userID=" + userID + "}\n");
                            }
                            return userID;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error while accessing the database");
            e.printStackTrace();
            return -1;
        }
    }

    public void delete() {
        try (Connection conn = DriverManager.getConnection(Database.DB_URL, Database.USER, Database.PASS);
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM User WHERE userID = ?")) {
            stmt.setInt(1, userID);
            stmt.executeUpdate();
            System.out.println("User deleted from the database");
        } catch (SQLException e) {
            System.out.println("Error while deleting user from the database");
            e.printStackTrace();
        }
    }

    public int getUserID() {
        return userID;
    }
}