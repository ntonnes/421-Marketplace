import java.sql.*;

public class Login {
    // Database connection details
    private static final String DB_URL = "jdbc:db2://winter2024-comp421.cs.mcgill.ca:50000/COMP421";
    private static final String USER = "cs421g10";
    private static final String PASS = "Cat!Dog923";

    // Class to hold name, userID, and email
    public static class User {
        public final String name;
        public final String userID;
        public final String email;

        public User(String name, String userID, String email) {
            this.name = name;
            this.userID = userID;
            this.email = email;
        }
    }

    // Method to validate user credentials
    public static User validate(String email, String password) {
        try {
            Class.forName("com.ibm.db2.jcc.DB2Driver"); // Load the DB2 JDBC driver

            // Establish a connection with the database
            try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
                // Prepare SQL query to fetch user with matching email and password
                String sql = "SELECT * FROM Customer WHERE email = ? AND password = ?";
                PreparedStatement statement = conn.prepareStatement(sql);
                statement.setString(1, email);
                statement.setString(2, password);

                ResultSet resultSet = statement.executeQuery();

                // Return User object if a matching user was found, null otherwise
                if (resultSet.next()) {
                    return new User(resultSet.getString("name"), resultSet.getString("userID"), email); // Include email in User object
                } else {
                    return null;
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