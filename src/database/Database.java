package database;
import users.User;

import java.sql.*;

public class Database {
    private static final String DB_URL = "jdbc:db2://winter2024-comp421.cs.mcgill.ca:50000/COMP421";
    private static final String USER = "cs421g10";
    private static final String PASS = "Cat!Dog923";
    public static Connection db;

    public static void connect() {
        try {
            db = DriverManager.getConnection(DB_URL, USER, PASS);
        } catch (SQLException e) {
            System.out.println("Error while connecting to the database");
            e.printStackTrace();
        }
    }

    public static void disconnect() {
        try {
            db.close();
        } catch (SQLException e) {
            System.out.println("Error while disconnecting from the database");
            e.printStackTrace();
        }
    }

    public static void createCustomer(User user, String name, String email, String password, String dob) {
        try {
            try (PreparedStatement insertStmt = db.prepareStatement("INSERT INTO Customer (userID, Name, Email, Password, DOB) VALUES (?, ?, ?, ?, ?)")) {
                insertStmt.setInt(1, user.userID);
                insertStmt.setString(2, dob);
                insertStmt.setString(3, password);
                insertStmt.setString(4, email);
                insertStmt.setString(5, name);
                insertStmt.executeUpdate();
                db.commit();
            }
        } catch (SQLException e) {
            System.out.println("Error while creating customer in the database");
            e.printStackTrace();
        }
    }

    public static boolean emailIsUnique(String email) {
        try {
            try (PreparedStatement checkStmt = db.prepareStatement("SELECT * FROM Customer WHERE Email = ?")) {
                checkStmt.setString(1, email);

                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        System.out.println("An account with that email already exists.");
                        return false;
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error while checking if email is unique");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static void deleteUser(User user) {
        try {
            try (PreparedStatement deleteStmt = db.prepareStatement("DELETE FROM User WHERE userID = ?")) {
                deleteStmt.setInt(1, user.userID);
                deleteStmt.executeUpdate();
                db.commit();
            }
        } catch (SQLException e) {
            System.out.println("Error while deleting user from the database");
            e.printStackTrace();
        }
    }

    public static int getNewUserID() {
        try {
            while (true) {
                int userID = (int) ((Math.random() * 9 + 1) * 100000000);  // Generate a 9-digit integer

                try (PreparedStatement checkStmt = db.prepareStatement("SELECT userID FROM User WHERE userID = ?")) {
                    checkStmt.setInt(1, userID);

                    try (ResultSet rs = checkStmt.executeQuery()) {
                        if (!rs.next()) {  // If the userID doesn't exist in the database
                            try (PreparedStatement insertStmt = db.prepareStatement("INSERT INTO User (userID) VALUES (?)")) {
                                insertStmt.setInt(1, userID);
                                insertStmt.executeUpdate();
                                db.commit();
                            }

                            return userID;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error while accessing the database");
            e.printStackTrace();
        }

        return -1;  // Return -1 if an error occurred
    }
}