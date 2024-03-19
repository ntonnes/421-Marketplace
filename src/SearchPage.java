import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import database.Database;
import database.users.User;
import main.Main;
import pages.search.SearchSelect;

public class SearchPage implements Page {
    private int minRating = 0;
    private int maxRating = 10;
    private double minPrice = 0;
    private double maxPrice = 500;
    private String categories = null;
    private String brands = null;
    private Integer modelID = null;
    private String sortBy = "ORDER BY modelID ASC";

    private String[][] data;

    SearchPage() {
    }

    @Override
    public void display(User user) {
        System.out.println("Search for a product:");
        System.out.println("Current Parameters:");
        if (modelID != null) {
            System.out.println("\tModel ID: " + modelID);
        }
        if (categories != null) {
            System.out.println("\tCategories included: " + categories);
        }
        if (brands != null) {
            System.out.println("\tBrands included: " + brands);
        }
        if (maxRating != 10 || minRating != 0) {
            System.out.println("\tRating: " + minRating + " - " + maxRating);
        }
        if (maxPrice != 500 || minPrice != 0) {
            System.out.println("\tPrice: " + minPrice + " - " + maxPrice);
        }
        System.out.println("Results sorted by: " + sortBy);
        System.out.println("\t(1) Add a search parameter\n\t(2) Search\n\t(3)");
    }

    @Override
    public void go(int option) {
        switch (option) {
            case 1:
                addParameter();
                break;
            case 2: 
                setSortBy();
                break;
            case 3:
                submit();
                break;
            case 4:
                break;
            default:
                System.out.println("Invalid option. Try again.");
        }
        System.out.println("Search results:");
    }

    private void addParameter(){
        System.out.println("Add a search parameter:");
        System.out.println("\t(1) Model ID\n\t(2) Categories\n\t(3) Brands\n\t(4) Rating\n\t(5) Price\n\t(6) \n\t(7) Go Back");
        int option = App.scanner.nextInt();
        switch (option) {
            case 1:
                System.out.println("Enter the model ID: ");
                modelID = App.scanner.nextInt();
                if (modelID > 999999999 || modelID < 100000000) {
                    System.out.println("Invalid model ID. Try again.");
                    modelID = null;
                }
                break;
            case 2:
                System.out.println("Enter the categories to include (separated by commas): ");
                categories = App.scanner.nextLine();
                break;
            case 3:
                System.out.println("Enter the brands to include (separated by commas): ");
                brands = App.scanner.nextLine();
                break;
            case 4:
                System.out.println("Enter the minimum rating (0-10): ");
                int minR = App.scanner.nextInt();
                if (minR < 0 || minR > 10) {
                    System.out.println("Invalid rating. Try again.");
                    break;
                }
                minRating = minR;
                System.out.println("Enter the maximum rating (0-10): ");
                int maxR = App.scanner.nextInt();
                if (maxR < minRating || maxR > 10) {
                    System.out.println("Invalid rating. Try again.");
                    break;
                }
                maxRating = maxR;
                break;
            case 5:
                System.out.println("Enter the minimum price (minimum 0): ");
                double minP = App.scanner.nextDouble();
                if (minP < 0) {
                    System.out.println("Invalid price. Try again.");
                    break;
                }
                System.out.println("Enter the maximum price: ");
                double maxP = App.scanner.nextDouble();
                if (maxP < minP) {
                    System.out.println("Invalid price. Try again.");
                    break;
                }
                break;
            case 6:
                break;
            default:
                System.out.println("Invalid option. Try again.");
        }
    }

    private void setSortBy() {
        System.out.println("Sort by:");
        System.out.println("\t(1) Model ID\n\t(2) Price High to Low\n\t(3) Price Low to High \n\t(4) Rating High to Low\n\t(5) \n\t(6) Go Back");
        int option = App.scanner.nextInt();
        switch (option) {
            case 1:
                sortBy = "modelID";
                break;
            case 2:
                sortBy = "price";
                break;
            case 3:
                sortBy = "brand";
                break;
            case 4:
                sortBy = "stars";
                break;
            case 5:
                sortBy = "productsSold";
                break;
            case 6:
                break;
            default:
                System.out.println("Invalid option. Try again.");
        }
    }

    private void submit() {
        List<String[]> dataList = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT Model.modelID, Model.price, BrandPage.name AS brand, Model.stars, COUNT(Purchased.modelID) AS productsSold " +
            "FROM Model " +
            "JOIN BrandPage ON Model.url = BrandPage.url " +
            "LEFT JOIN Purchased ON Model.modelID = Purchased.modelID " +
            "WHERE 1=1");

        sql.append(" AND Model.stars >= " + minRating);
        sql.append(" AND Model.stars <= " + maxRating);
        if (brands != null) {
            sql.append(" AND BrandPage.name IN (" + brands + ")");
        }
        sql.append(" AND Model.price >= " + minPrice);
        sql.append(" AND Model.price <= " + maxPrice);
        if (modelID != null) sql.append(" AND Model.modelID = " + modelID);
        if (categories != null) {
            sql.append(" AND Model.modelID IN (SELECT modelID FROM ModelCategory WHERE category IN (" + categories + "))");
        }
        sql.append(" GROUP BY Model.modelID, Model.price, BrandPage.name, Model.stars");
        sql.append(" "+ sortBy);
        
        try (Connection conn = DriverManager.getConnection(Database.DB_URL, Database.USER, Database.PASS);
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            ResultSet rs = stmt.executeQuery();
        
            while (rs.next()) {
                String stars = rs.getString("stars");
                if (stars == null) {
                    stars = "0";
                }
                String[] row = {
                    rs.getString("modelID"),
                    rs.getString("price"),
                    rs.getString("brand"),
                    stars,
                    String.valueOf(rs.getInt("productsSold"))
                };
                dataList.add(row);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while retrieving models from the database: " + e.getMessage());
        }

        data = dataList.toArray(new String[0][]);
        printData();
    }

    private void printData(){
        System.out.println("Search successful. Parameters:");
        
        System.out.println("    Model ID: " + modelID);
        System.out.println("    Price: " + minPrice + " - " + maxPrice);
        System.out.println("    Stars: $" + minRating + " - $" + maxRating);
        System.out.println("    Brands: " + brands);
        System.out.println("    Categories: " + categories);
        System.out.println("\nData:");
        for (String[] row : data) {
            System.out.println("    " + Arrays.toString(row));
        }
    }
}
