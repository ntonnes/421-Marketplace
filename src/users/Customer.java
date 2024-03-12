package users;

import java.sql.*;
import java.util.*;

import database.*;

public class Customer extends User {
    private String name;
    private String email;
    private String password;
    private String dob;
    private Model[] modelsOrdered;
    private Order[] orderList;


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

    private void setOrderList() {
        List<Order> orders = new ArrayList<>();
        try {
            PreparedStatement stmt = Database.db.prepareStatement("SELECT OrderID FROM Order WHERE Email = ?");
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

    private void setModelsOrdered() {
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