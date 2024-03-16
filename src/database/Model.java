package database;

import java.sql.*;
<<<<<<< Updated upstream
=======
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
>>>>>>> Stashed changes

public class Model {
    private int modelID;
    private double price;
    private String url;
    private String brand;
    private double stars;

    public Model(int modelID) {
        try (Connection conn = DriverManager.getConnection(Database.DB_URL, Database.USER, Database.PASS);
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Model WHERE modelID = ?")) {
            
            stmt.setInt(1, modelID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                this.modelID = rs.getInt("modelID");
                this.price = rs.getDouble("price");
                this.url = rs.getString("url");
                this.brand = rs.getString("brand");
                this.stars = rs.getDouble("stars");
            }
        } catch (SQLException e) {
            System.out.println("Error while retrieving model from the database");
            e.printStackTrace();
        }
    }


<<<<<<< Updated upstream
=======
                models.put(modelID, this);
            } else {
                throw new ModelError("Model with modelID " + modelID + " does not exist in the 'Model' table.");
            }
        } catch (SQLException e) {
            throw new ModelError("Error while retrieving model from the database: " + e.getMessage());
        }
    }
    
    // Constructor for adding model to the database
    private Model(int modelID, double price, String url, String brand, double stars, Connection conn) throws ModelError {
        this.modelID = modelID;
        this.price = price;
        this.url = url;
        this.brand = brand;
        this.stars = stars;
    
        try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO Model (modelID, price, url, brand, stars) VALUES (?, ?, ?, ?, ?)")) {
            stmt.setInt(1, modelID);
            stmt.setDouble(2, price);
            stmt.setString(3, url);
            stmt.setString(4, "421Marketplace.com/"+brand);
            stmt.setDouble(5, stars);
            stmt.executeUpdate();
            models.put(modelID, this);
        } catch (SQLException e) {
            throw new ModelError("Error while adding model to the database: " + e.getMessage());
        }
    }
    
    // Constructor for a new model
    public static Model addModel(int modelID, double price, String url, String brand, double stars, Connection conn) {
        if (models.containsKey(modelID)) {
            System.out.println("Model with modelID " + modelID + " already exists in the 'Model' table. Returning existing model.");
            return models.get(modelID);
        } else {
            try {
                Model model = new Model(modelID, price, url, brand, stars, conn);
                System.out.println("Added model with modelID " + modelID + " to the 'Model' table. Returning model.");
                return model;
            } catch (ModelError e) {
                System.out.println(e.getMessage());
                return null;
            }
        }
    }
    
    public static Model getModel(int modelID, Connection conn) {
        if (models.containsKey(modelID)) {
            System.out.println("Model with modelID " + modelID + " found in the cache. Returning model.");
            return models.get(modelID);
        } else {
            try {
                Model model = new Model(modelID, conn);
                System.out.println("Retrieved model with modelID " + modelID + " from the 'Model' table and added to the cache. Returning model.");
                return model;
            } catch (ModelError e) {
                System.out.println(e.getMessage());
                return null;
            }
        }
    }

    public static String[][] getModelSearch(Connection conn, Double minStars, Double maxStars, String brand, Double minPrice, Double maxPrice, Integer modelID) {
        List<String[]> data = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
            "SELECT Model.modelID, Model.price, BrandPage.name AS brandName, Model.stars " +
            "FROM Model " +
            "JOIN BrandPage ON Model.url = BrandPage.url WHERE 1=1");

        if (minStars != null) sql.append(" AND Model.stars >= ?");
        if (maxStars != null) sql.append(" AND Model.stars <= ?");
        if (brand != null) sql.append(" AND BrandPage.name = ?");
        if (minPrice != null) sql.append(" AND Model.price >= ?");
        if (maxPrice != null) sql.append(" AND Model.price <= ?");
        if (modelID != null) sql.append(" AND Model.modelID = ?");

        try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            int paramIndex = 1;
            if (minStars != null) stmt.setDouble(paramIndex++, minStars);
            if (maxStars != null) stmt.setDouble(paramIndex++, maxStars);
            if (brand != null) stmt.setString(paramIndex++, brand);
            if (minPrice != null) stmt.setDouble(paramIndex++, minPrice);
            if (maxPrice != null) stmt.setDouble(paramIndex++, maxPrice);
            if (modelID != null) stmt.setInt(paramIndex++, modelID);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String[] row = {
                    rs.getString("modelID"),
                    rs.getString("price"),
                    rs.getString("brandName"),
                    rs.getString("stars")
                };
                data.add(row);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while retrieving models from the database: " + e.getMessage());
        }

        return data.toArray(new String[0][]);
    }
>>>>>>> Stashed changes
    
    public int getModelID() {
        return modelID;
    }
    public double getPrice() {
        return price;
    }
    public String getUrl() {
        return url;
    }
    public String getBrand() {
        return brand;
    }
    public double getStars() {
        return stars;
    }

    @Override
    public String toString() {
        return "Model{" +
                "modelID=" + modelID +
                ", price=" + price +
                ", url='" + url + '\'' +
                ", brand='" + brand + '\'' +
                ", stars=" + stars +
                '}';
    }
}