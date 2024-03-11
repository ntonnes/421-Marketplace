import java.util.Scanner;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

//The add to Cart task
public class Task2 {
    //Need to change all of these to connect to our database:
    static final String DB_URL = "jdbc:mysql://localhost/TUTORIALSPOINT";
    static final String USER = "guest";
    static final String PASS = "guest123";
    public static void TaskCode() {
        Scanner scanner = new Scanner(System.in);
        int userID;
        int ModelID;
        Boolean good = false;
        String goodModel;
        int Amount = 0;
        String confirmation;

        userID = scanner.nextInt();
        System.out.print("Please enter your userID:");
        System.out.println();

        while(!good) {
            ModelID = scanner.nextInt();
            System.out.print("What model do you want to add to your cart?");

            try(Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
                Statement stmt = conn.createStatement();
                //Get the corresponding model
                ResultSet checkModel = stmt.executeQuery("SELECT * FROM Model WHERE ModelID = " + ModelID);
            ) {
                if (checkModel.next()) {
                    int modelID = checkModel.getInt("ModelID");
                    double price = checkModel.getDouble("Price");
                    String url = checkModel.getString("URL");
                    int stars = checkModel.getInt("Stars");

                    System.out.println("ModelID: " + modelID);
                    System.out.println("Price: " + price);
                    System.out.println("URL: " + url);
                    System.out.println("Stars: " + stars);

                    goodModel = scanner.next();
                    System.out.print("Is this the right model? (yes or no)");
                    System.out.println();
                    if (goodModel.equals("yes")) {
                        //Now, we get the number of available products for wanted model
                        try (Statement stmt2 = conn.createStatement();
                             ResultSet numberForModel = stmt2.executeQuery("SELECT ModelID, COUNT(*) AS NumberOfProducts FROM Product WHERE ModelID = " + checkModel);
                             ) {
                            int amountAvailable = numberForModel.getInt("NumberOfProducts");
                            Boolean goodAmount = false;
                            while(!goodAmount) {
                                System.out.println("For the desired model, there are " + numberForModel + " products available");
                                Amount = scanner.nextInt();
                                System.out.print("How many do you want?");
                                System.out.println();
                                if (0 < Amount && Amount <= amountAvailable) {
                                    goodAmount = true;
                                }
                            }
                            System.out.println("Adding to cart: " + Amount + " copies of the Model " + ModelID + " for a total of " + Amount*price);
                            confirmation = scanner.next();
                            System.out.print("Do you confirm? (yes or no)");
                            System.out.println();
                            if (confirmation.equals("yes")) {
                                try (Statement stmt3 = conn.createStatement()) {
                                    // Check if the user already has the model in their cart
                                    ResultSet existingCart = stmt3.executeQuery("SELECT * FROM InCart WHERE UserID = " + userID + " AND ModelID = " + ModelID);
                                    if (existingCart.next()) {
                                        // User already has the model in their cart, update the number of copies
                                        int currentCopies = existingCart.getInt("Copies");
                                        int newCopies = currentCopies + Amount;
                                        stmt3.executeUpdate("UPDATE InCart SET Copies = " + newCopies + " WHERE UserID = " + userID + " AND ModelID = " + ModelID);
                                        System.out.println("Updated cart: " + newCopies + " copies of Model " + ModelID + " in the cart.");
                                    } else {
                                        // User does not have the model in their cart, insert a new tuple
                                        stmt3.executeUpdate("INSERT INTO InCart (UserID, ModelID, Copies) VALUES (" + userID + ", " + ModelID + ", " + Amount + ")");
                                        System.out.println("Added to cart: " + Amount + " copies of Model " + ModelID + ".");
                                    }
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                } else {
                    System.out.println("No model found with ID: " + ModelID);
                }
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            }
        }
    }


