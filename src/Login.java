import java.sql.*;

public class Login {
    private static final String DB_URL = "jdbc:db2://winter2024-comp421.cs.mcgill.ca:50000/COMP421";
    private static final String USER = "cs421g10";
    private static final String PASS = "Cat!Dog923";

    public static boolean validate(String email, String password) {
        try {
            Class.forName("com.ibm.db2.jcc.DB2Driver"); // Load the DB2 JDBC driver

            try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
                String sql = "SELECT * FROM Customer WHERE email = ? AND password = ?";
                PreparedStatement statement = conn.prepareStatement(sql);
                statement.setString(1, email);
                statement.setString(2, password);

                ResultSet resultSet = statement.executeQuery();

                // No matching user was found
                return resultSet.next(); // A matching user was found
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String getName(String email) {
        try {
            Class.forName("com.ibm.db2.jcc.DB2Driver"); // Load the DB2 JDBC driver

            try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
                String sql = "SELECT name FROM Customer WHERE email = ?";
                PreparedStatement statement = conn.prepareStatement(sql);
                statement.setString(1, email);

                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    return resultSet.getString("name"); // Return the name of the matching user
                } else {
                    return null; // No matching user was found
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}