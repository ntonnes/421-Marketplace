package database;
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