package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    public static final String DB_URL = "SOCSURL";
    public static final String USER = "SOCSUSER";
    public static final String PASS = "SOCSPASSWD";

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