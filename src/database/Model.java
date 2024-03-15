package database;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Model {
    private static Map<Integer, Model> models = new HashMap<>();
    private int modelID;
    private double price;
    private String url;
    private String brand;
    private double stars;

    public class ModelError extends Exception {
        public ModelError(String message) {
            super(message);
        }
    }

    // Constructor for retrieving model from the database
    protected Model (int modelID, Connection conn) throws ModelError {
        try (PreparedStatement stmt = conn.prepareStatement(
            "SELECT Model.*, BrandPage.name AS brandName " +
            "FROM Model " +
            "JOIN BrandPage ON Model.url = BrandPage.url " +
            "WHERE Model.modelID = ?")) {
            stmt.setInt(1, modelID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                this.modelID = rs.getInt("modelID");
                this.price = rs.getDouble("price");
                this.url = rs.getString("url");
                this.stars = rs.getDouble("stars");
                this.brand = rs.getString("brandName");

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Model model = (Model) o;
        return modelID == model.modelID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(modelID);
    }

}