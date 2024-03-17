package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Order {
    private static Map<Integer, Order> orders = new HashMap<>();
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

    // Constructor for retrieving order from the database
    private Order(int orderID, Connection conn) {
        if (orders.containsKey(orderID)) {
            Order order = orders.get(orderID);
            this.orderID = order.orderID;
            this.deliverAdd = order.deliverAdd;
            this.total = order.total;
            this.date = order.date;
            this.email = order.email;
            this.cardNum = order.cardNum;
            this.productsOrdered = order.productsOrdered;
            this.modelsOrdered = order.modelsOrdered;
        } else {
            try (PreparedStatement stmt1 = conn.prepareStatement("SELECT * FROM Order WHERE orderID = ?");
                 PreparedStatement stmt2 = conn.prepareStatement("SELECT SerialNo, ModelID FROM Purchased WHERE OrderID = ?")) {

                // Retrieve order details from the 'Order' table
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

                // Retrieve products ordered in this order from the 'Purchased' table
                stmt2.setInt(1, orderID);
                rs = stmt2.executeQuery();
                List<Product> products = new ArrayList<>();
                Set<Model> models = new HashSet<>();
                while (rs.next()) {
                    int modelID = rs.getInt("ModelID");
                    int serialNo = rs.getInt("SerialNo");
                    Product product = Product.getProduct(modelID, serialNo, conn);
                    products.add(product);
                    models.add(Model.getModel(modelID, conn));
                }

                this.productsOrdered = products.toArray(new Product[0]);
                this.modelsOrdered = models.toArray(new Model[0]);
                orders.put(orderID, this);
            } catch (SQLException e) {
                System.out.println("Error while retrieving order from the database");
                e.printStackTrace();
            }
        }
    }


    public static Order getOrder(int orderID, Connection conn) {
        if (orders.containsKey(orderID)) {
            System.out.println("Order " + orderID + " was found in the cache. Returning order.");
            return orders.get(orderID);
        } else {
            Order order = new Order(orderID, conn);
            System.out.println("Order " + orderID + " was found in the 'Order' table and added to the cache. Returning order.");
            return order;
        }
    }

    public static Order[] getCustomerOrders(String email, Connection conn) {
        List<Order> orders = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement("SELECT OrderID FROM Order WHERE Email = ?")) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                try {
                    orders.add(getOrder(rs.getInt("OrderID"), conn));
                } catch (Exception e) {
                    System.out.println("Error while retrieving order: " + e.getMessage());
                }
            }
        } catch (SQLException e) {
            System.out.println("Error while retrieving orders from the database");
            e.printStackTrace();
        }
        return orders.isEmpty() ? new Order[0] : orders.toArray(new Order[0]);
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
        StringBuilder productsOrderedStr = new StringBuilder("    Products Ordered: {\n");
        for (Product product : productsOrdered) {
            productsOrderedStr.append("        (ModelID: ").append(product.getModelID())
                              .append(", SerialNo: ").append(product.getSerialNo()).append(");\n");
        }
        productsOrderedStr.append("    }\n");
    
        StringBuilder modelsOrderedStr = new StringBuilder("    Unique Models Ordered {\n");
        for (Model model : modelsOrdered) {
            modelsOrderedStr.append("        ModelID: ").append(model.getModelID()).append(";\n");
        }
        modelsOrderedStr.append("    }\n");

        return "Order " + orderID + " {\n" +
            "    Delivery Address: " + deliverAdd + '\n' +
            "    Total: $" + total + '\n' +
            "    Order Date: " + date + '\n' +
            "    Email: " + email + '\n' +
            "    Card Number: " + cardNum + '\n' +
            productsOrderedStr +
            modelsOrderedStr +
            '}';
    }
}
