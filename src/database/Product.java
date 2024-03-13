package database;

import java.sql.*;

public class Product extends Model {
    private int serialNo;
    private Boolean returned;
    private String supplierName;
    private int restockNo;

    public Product(int modelID, int serialNo) {
        // inherits all methods and properties of models
        super(modelID);
        try (Connection conn = DriverManager.getConnection(Database.DB_URL, Database.USER, Database.PASS);
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Product WHERE serialNo = ?")) {

            stmt.setInt(1, serialNo);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                this.serialNo= rs.getInt("serialNo");
                this.returned = rs.getBoolean("return");
                this.supplierName = rs.getString("supplierName");
                this.restockNo = rs.getInt("restockNo");
            }
        } catch (SQLException e) {
            System.out.println("Error while retrieving product from the database");
            e.printStackTrace();
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