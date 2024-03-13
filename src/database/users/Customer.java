package database.users;

import java.sql.*;
import java.util.*;

import database.*;
import main.Main;

public class Customer extends User {
    protected String name;
    protected String email;
    protected String password;
    protected String dob;
    protected Model[] modelsOrdered;
    protected Order[] orderList;


    public Customer(int userID, String name, String email, String password, String dob) {
        super(userID);
        this.name = name;
        this.email = email;
        this.password = password;
        this.dob = dob;
        this.modelsOrdered = new Model[0];
        this.orderList = new Order[0];
        setModelsOrdered();
        setOrderList();
    }

    protected void setOrderList() {
        List<Order> orders = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(Database.DB_URL, Database.USER, Database.PASS);
        PreparedStatement stmt = conn.prepareStatement("SELECT OrderID FROM Order WHERE Email = ?")) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                orders.add(new Order(rs.getInt("OrderID")));
            }
        } catch (SQLException e) {
            System.out.println("Error while retrieving orders from the database");
            e.printStackTrace();
        }
        orderList = orders.isEmpty() ? new Order[0] : orders.toArray(new Order[0]);
    }

    protected void setModelsOrdered() {
        Set<Model> models = new HashSet<>();
        if (this.orderList != null) {
            for (Order order : this.orderList) {
                Model[] orderedModels = order.getModelsOrdered();
                if (orderedModels != null) {
                    for (Model model : orderedModels) {
                        models.add(model);
                    }
                }
            }
        }
        modelsOrdered = models.isEmpty() ? new Model[0] : models.toArray(new Model[0]);
    }

    public void logout() {
        System.out.println("Successfully logged out user " + name + ":\n" + this.toString() + "\n");
        Main.setUser(new User()); // Set the current user to a guest user
    }


    public void refreshOrdered() {
        setOrderList();
        setModelsOrdered();
    }

    public String getName() {
        return name;
    }
    public String getEmail() {
        return email;
    }
    public String getPassword() {
        return password;
    }
    public String getDob() {
        return dob;
    }
    public Model[] getModelsOrdered() {
        return modelsOrdered != null ? modelsOrdered : new Model[0];
    }
    public Order[] getOrderList() {
        return orderList != null ? orderList : new Order[0];
    }

    @Override
    public String toString() {
        return "Customer{" +
            "userID=" + userID +
            ", name='" + name + '\'' +
            ", email='" + email + '\'' +
            ", password='" + password + '\'' +
            ", dob='" + dob + '\'' +
            ", modelsOrdered=" + Arrays.toString(modelsOrdered) +
            ", orderList=" + Arrays.toString(orderList) +
            '}';
    }
}