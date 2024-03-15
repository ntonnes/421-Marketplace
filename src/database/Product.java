package database;

import java.sql.*;
import java.util.*;

public class Product extends Model {
    private static Map<ProductKey, Product> products = new HashMap<>();
    private int serialNo;
    private Boolean returned;
    private String supplierName;
    private int restockNo;

    // Custom exception for product errors
    private class ProductError extends Exception {
        public ProductError(String message) {
            super(message);
        }
    }

    // Constructor for retrieving product from the database
    private Product(int modelID, int serialNo, Connection conn) throws Exception {
        super(modelID, conn);
        try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Product WHERE serialNo = ?")) {
            stmt.setInt(1, serialNo);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                this.serialNo= rs.getInt("serialNo");
                this.returned = rs.getBoolean("return");
                this.supplierName = rs.getString("supplierName");
                this.restockNo = rs.getInt("restockNo");
                products.put(new ProductKey(modelID, this.serialNo), this);
            } else {
                throw new ProductError("Product with modelID " + modelID + " and serialNo " + serialNo + " does not exist in the 'Product' table.");
            }
        } catch (SQLException e) {
            throw new ProductError("Error while retrieving product from the database: " + e.getMessage());
        }
    }

    // Constructor for adding product to the database
    private Product(int modelID, int serialNo, Boolean returned, String supplierName, int restockNo, Connection conn) throws Exception {
        super(modelID, conn);
        this.serialNo = serialNo;
        this.returned = returned;
        this.supplierName = supplierName;
        this.restockNo = restockNo;

        try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO Product (modelID, serialNo, return, supplierName, restockNo) VALUES (?, ?, ?, ?, ?)")) {
            stmt.setInt(1, modelID);
            stmt.setInt(2, serialNo);
            stmt.setBoolean(3, returned);
            stmt.setString(4, supplierName);
            stmt.setInt(5, restockNo);
            stmt.executeUpdate();
            products.put(new ProductKey(modelID, serialNo), this);
        } catch (SQLException e) {
            throw new ProductError("Error while adding product to the database: " + e.getMessage());
        }
    }

    // Method to add a product to the database
    public static Product addProduct(int modelID, int serialNo, Boolean returned, String supplierName, int restockNo, Connection conn) {
        ProductKey key = new ProductKey(modelID, serialNo);

        if (products.containsKey(key)) {
            System.out.println("Product with modelID " + modelID + " and serialNo " + serialNo + " already exists in the 'Product' table. Returning existing product.");
            return products.get(key);

        } else {
            try {
                Product product = new Product(modelID, serialNo, returned, supplierName, restockNo, conn);
                System.out.println("Added product with modelID " + modelID + " and serialNo " + serialNo + " to the 'Product' table. Returning product.");
                return product;

            } catch (Exception e) {
                System.out.println(e.getMessage());
                return null;
            }
        }
    }

    // Method to retrieve a product from the hashset or the database if it doesn't exist
    public static Product getProduct(int modelID, int serialNo, Connection conn) {
        ProductKey key = new ProductKey(modelID, serialNo);

        if (products.containsKey(key)) {
            System.out.println("Product with modelID " + modelID + " and serialNo " + serialNo + " found in the cache. Returning product.");
            return products.get(key);
        } else {
            try {
                Product product = new Product(modelID, serialNo, conn);
                System.out.println("Retrieved product with modelID " + modelID + " and serialNo " + serialNo + " from the 'Product' table and added to the cache. Returning product.");
                return product;
            } catch (Exception e) {
                System.out.println(e.getMessage());
                return null;
            }
        }
    }


    public int getSerialNo() {
        return serialNo;
    }
    public Boolean getReturned() {
        return returned;
    }
    public String getSupplierName() {
        return supplierName;
    }
    public int getRestockNo() {
        return restockNo;
    }
    @Override
    public String toString() {
        return "Product{" +
                "modelID=" + getModelID() +
                ", serialNo=" + serialNo +
                ", returned=" + returned +
                ", supplierName='" + supplierName + '\'' +
                ", restockNo=" + restockNo +
                '}';
    }
}