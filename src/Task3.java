import java.util.Scanner;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Task3 {
    //Need to change all of these to connect to our database:
    static final String DB_URL = "jdbc:mysql://localhost/TUTORIALSPOINT";
    static final String USER = "guest";
    static final String PASS = "guest123";
    public static void TaskCode() {
        Scanner scanner = new Scanner(System.in);
        String category;
        String choice;
        String sqlQuery;
        try(Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement stmt = conn.createStatement();
            Statement stmt2 = conn.createStatement();
            //Get the corresponding model
            ResultSet allCategories = stmt.executeQuery("SELECT CName FROM Category");
        ) {
            System.out.println("These are all the available categories:");
            while (allCategories.next()) {
                String cname = allCategories.getString("CName");
                System.out.println(cname);
            }
            System.out.println();
            System.out.println("Which category do you want? (select only one)");
            category = scanner.next();

            System.out.println("Do you want the models ordered by price or stars? (price or stars");
            choice = scanner.next();

            if (choice.equals("stars")) {
                // Construct SQL query to retrieve models of the desired category ordered by stars
                sqlQuery = "SELECT Model.ModelID, Model.Price, Model.Stars " +
                        "FROM Model " +
                        "LEFT JOIN Belongs ON Model.ModelID = Belongs.ModelID " +
                        "LEFT JOIN Category ON Belongs.CName = Category.CName " +
                        "WHERE Category.CName = '" + category + "' ORDER BY COALESCE(Model.Stars, 0) DESC";
            } else {
                sqlQuery = "SELECT Model.ModelID, Model.Price, Model.Stars " +
                        "FROM Model " +
                        "LEFT JOIN Belongs ON Model.ModelID = Belongs.ModelID " +
                        "LEFT JOIN Category ON Belongs.CName = Category.CName " +
                        "WHERE Category.CName = '" + category + "' ORDER BY Model.Price ASC";
            }

            ResultSet modelsOfCat = stmt2.executeQuery(sqlQuery);

            while (modelsOfCat.next()) {
                int modelID = modelsOfCat.getInt("ModelID");
                double price = modelsOfCat.getDouble("Price");
                String url = modelsOfCat.getString("URL");
                double stars = modelsOfCat.getDouble("Stars");
                System.out.println("ModelID: " + modelID + ", Price: $" + price + ", URL: " + url + ", Stars: " + stars);
            }
            conn.close();
            scanner.close();
        } catch (SQLException e) {
        e.printStackTrace();
        }
    }
}
