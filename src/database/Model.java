package database;

import java.sql.*;
import java.util.Objects;

public class Model {
    private int modelID;
    private double price;
    private String url;
    private String brand;
    private double stars;

    public Model(int modelID) {
        try (Connection conn = DriverManager.getConnection(Database.DB_URL, Database.USER, Database.PASS);
        PreparedStatement stmt = conn.prepareStatement(
            "SELECT Model.*, BrandPage.name AS brand " +
            "FROM Model " +
            "LEFT JOIN BrandPage ON Model.url = BrandPage.url " +
            "WHERE Model.modelID = ?"
        )) {
            
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