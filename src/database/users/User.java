package database.users;
import java.sql.*;

import database.Database;
import main.Main;

public class User {
    protected int userID;
    
    // Constructor for a new guest user
    public User() {
        this.userID = getUniqueUserID();
    }
    // Constructor for an existing user
    public User(int userID) {
        this.userID = userID;
        createUser(userID);
    }

    private int getUniqueUserID() {
        try (Connection conn = DriverManager.getConnection(Database.DB_URL, Database.USER, Database.PASS)) {
            while (true) {
                int userID = (int) ((Math.random() * 9 + 1) * 100000000);  // Generate a 9-digit integer

                try (PreparedStatement checkStmt = conn.prepareStatement("SELECT userID FROM User WHERE userID = ?")) {
                    checkStmt.setInt(1, userID);

                    try (ResultSet rs = checkStmt.executeQuery()) {
                        if (!rs.next() && userID != Main.DEBUG_USERID) {  // If the userID doesn't exist in the database
                            try (PreparedStatement insertStmt = conn.prepareStatement("INSERT INTO User (userID) VALUES (?)")) {
                                insertStmt.setInt(1, userID);
                                insertStmt.executeUpdate();
                                System.out.println("\nNew guest user added to the database:\nUser{userID=" + userID + "}\n");
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

    private void createUser(int userID) {
        try (Connection conn = Database.connect();
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO User (userID) VALUES (?)")) {
            stmt.setInt(1, userID);
            stmt.executeUpdate();
            System.out.println("\nNew guest user added to the database:\nUser{userID=" + userID + "}\n");
        } catch (SQLException e) {
            System.out.println("\nThe application closed unexpectedly in the last debug session.\n"+
                               "User{userID=\" + userID + \"} already exists in the database.\n" +
                               "\nSetting the current user to User{userID=" + userID + "}.\n");
        }
    }

    public void delete() {
        int userID = Main.user.getUserID();
        try (Connection conn = DriverManager.getConnection(Database.DB_URL, Database.USER, Database.PASS);
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM User WHERE userID = ?")) {
            stmt.setInt(1, userID);
            stmt.executeUpdate();
            System.out.println("User " + userID + " deleted from the database\n");
        } catch (SQLException e) {
            System.out.println("Error while deleting user from the database");
            e.printStackTrace();
        }
    }

    public int getUserID() {
        return userID;
    }
}