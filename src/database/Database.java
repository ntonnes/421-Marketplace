package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    public static final String DB_URL = "jdbc:db2://winter2024-comp421.cs.mcgill.ca:50000/COMP421";
    public static final String USER = "cs421g10";
    public static final String PASS = "Cat!Dog923";

    public static Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
        } catch (SQLException e) {
            System.out.println("Error while accessing the database");
            e.printStackTrace();
        }
        return conn;
    }
}