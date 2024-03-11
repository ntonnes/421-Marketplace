import java.util.Scanner;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

//The add review task
public class Task1 {
    //Need to change all of these to connect to our database:
    static final String DB_URL = "jdbc:mysql://localhost/TUTORIALSPOINT";
    static final String USER = "guest";
    static final String PASS = "guest123";
    public static void TaskCode() {
        Scanner scanner = new Scanner(System.in);
        List<Integer> modelIDs = new ArrayList<>();
        String userEmail;
        int modelToReview;
        int rating;
        int userID;
        String message;
        String Choice;

        userID = scanner.nextInt();
        System.out.print("Please enter your userID:");
        System.out.println();

        userEmail = scanner.next();
        System.out.print("Please enter your Email:");
        System.out.println();


        try(Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement stmt = conn.createStatement();
            //Get all the orderID's of the User
            ResultSet ordersResult = stmt.executeQuery("SELECT OrderID FROM Orders WHERE Email = '" + userEmail + "'");

        ) {
            while (ordersResult.next()) {
                int orderID = ordersResult.getInt("OrderID");
                //Get all the ModelID associated with the orderID's
                try(Statement stmt2 = conn.createStatement();
                    ResultSet modelsResults = stmt2.executeQuery("SELECT ModelID FROM Purchased WHERE OrderID = " + orderID)) {

                    //Get all the models
                    while (modelsResults.next()) {
                        int modelId = modelsResults.getInt("ModelID");
                        //Make a list of all the modelID
                        modelIDs.add(modelId);
                    }
                }
            }
            //Let customer choose which model he would like to make a review on:
            System.out.println("Here is a list of all the models you can leave a review on:");
            for (int modelId : modelIDs) {
                System.out.println(modelId);
            }
            modelToReview = scanner.nextInt();
            System.out.print("On which model would you like to leave a review?");
            System.out.println();
            rating = scanner.nextInt();
            System.out.print("What rating do you want to give to model " + modelToReview);
            System.out.println();
            Choice = scanner.next();
            System.out.print("Would you like to leave a message with your review? (yes or no)");
            System.out.println();
            //If the user wants to leave a message
            if (Choice.equals("yes")) {
                message = scanner.next();
                System.out.print("What is the message: ");
                System.out.println();
                try(Statement stmt3 = conn.createStatement()) {
                    stmt3.executeUpdate("INSERT INTO Review (UserID, ModelID, rating, message) VALUES (" + userID + ", " + modelToReview + ", " + rating + ", '" + message + "')");
                }
            } //If the user doest want to leave a message
            else {
                try(Statement stmt4 = conn.createStatement()) {
                    stmt4.executeUpdate("INSERT INTO Review (UserID, ModelID, rating) VALUES (" + userID + ", " + modelToReview + ", " + rating + ")");
                }
            }
            conn.close();
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
