package database;

import java.sql.*;
import java.util.*;
import java.util.Date;

public class Order {
    private int orderID;
    private String deliverAdd;
    private double total;
    private Date date;
    private String email;
    private String cardNum;
    // Array of product objects in this order
    private Product[] productsOrdered;
    // Array of unique model objects in this order
    private Model[] modelsOrdered;

    public Order(int orderID) {
        try (Connection conn = DriverManager.getConnection(Database.DB_URL, Database.USER, Database.PASS);
            PreparedStatement stmt1 = conn.prepareStatement("SELECT * FROM Order WHERE orderID = ?");
            PreparedStatement stmt2 = conn.prepareStatement("SELECT SerialNo, ModelID FROM Purchased WHERE OrderID = ?")) {

            stmt1.setInt(1, orderID);
            ResultSet rs = stmt1.executeQuery();
            if (rs.next()) {
                this.orderID = rs.getInt("OrderID");
                this.deliverAdd = rs.getString("DeliverAdd");
                this.total = rs.getDouble("Total");
                this.date = rs.getDate("Date");
                this.email = rs.getString("Email");
                this.cardNum = rs.getString("CardNum");
            }

            stmt2.setInt(1, orderID);
            rs = stmt2.executeQuery();
            List<Product> products = new ArrayList<>();
            Set<Model> models = new HashSet<>();
            while (rs.next()) {
                Product product = new Product(rs.getInt("ModelID"), rs.getInt("SerialNo"));
                products.add(product);
                models.add(new Model(product.getModelID()));
            }

            this.productsOrdered = products.toArray(new Product[0]);
            this.modelsOrdered = models.toArray(new Model[0]);
        } catch (SQLException e) {
            System.out.println("Error while retrieving order from the database");
            e.printStackTrace();
        }
    }



    public int getOrderID() {
        return orderID;
    }
    public String getDeliverAdd() {
        return deliverAdd;
    }
    public double getTotal() {
        return total;
    }
    public Date getDate() {
        return date;
    }
    public String getEmail() {
        return email;
    }
    public String getCardNum() {
        return cardNum;
    }
    public Product[] getProductsOrdered() {
        return productsOrdered;
    }
    public Model[] getModelsOrdered() {
        return modelsOrdered;
    }

    @Override
    public String toString() {
        return "Order{" +
            "orderID=" + orderID +
            ", deliverAdd='" + deliverAdd + '\'' +
            ", total=" + total +
            ", date=" + date +
            ", email='" + email + '\'' +
            ", cardNum='" + cardNum + '\'' +
            ", productsOrdered=" + Arrays.toString(productsOrdered) +
            ", modelsOrdered=" + Arrays.toString(modelsOrdered) +
            '}';
    }
}
