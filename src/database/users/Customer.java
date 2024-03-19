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
    protected Set<Model> modelsOrdered;
    protected Order[] orderList;

    public Customer(int userID, String name, String email, String password, String dob) {
        super(userID);
        this.name = name;
        this.email = email;
        this.password = password;
        this.dob = dob;
        this.modelsOrdered = new HashSet<>();
        this.orderList = new Order[0];

        try (Connection conn = DriverManager.getConnection(Database.DB_URL, Database.USER, Database.PASS)) {
            orderList = Order.getCustomerOrders(email, conn);
            setModelsOrdered();
        } catch (SQLException e) {
            System.out.println("Error while accessing the database");
            e.printStackTrace();
        }
    }

    protected void setModelsOrdered() {
        if (this.orderList != null) {
            for (Order order : this.orderList) {
                Model[] orderedModels = order.getModelsOrdered();
                if (orderedModels != null) {
                    modelsOrdered.addAll(Arrays.asList(orderedModels));
                }
            }
        }
    }


    public void logout() {
        System.out.println("Successfully logged out user " + name + ":\n" + this.toString() + "\n");
        Main.setUser(new User()); // Set the current user to a guest user
    }

    public void refreshCustomerOrders(Connection conn) {
        orderList = Order.getCustomerOrders(email, conn);
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
        return modelsOrdered != null ? modelsOrdered.toArray(new Model[0]) : new Model[0];
    }
    public Order[] getOrderList() {
        return orderList != null ? orderList : new Order[0];
    }

    @Override
    public String toString() {
        StringBuilder modelsOrderedStr = new StringBuilder("\nUnique Models Ordered:\n");
        for (Model model : modelsOrdered) {
            modelsOrderedStr.append("    ModelID: ").append(model.getModelID()).append(";\n");
        }
        modelsOrderedStr.append("\n");

        StringBuilder orderListStr = new StringBuilder("Orders Placed:\n");
        for (Order order : orderList) {
            String orderStr = "    " + order.toString().replace("\n", "\n    ");
            orderListStr.append(orderStr).append(";\n");
        }
        orderListStr.append("}\n");

        return "\nCustomer:\n" +
            "    UserID: " + userID + '\n' +
            "    Name: " + name + '\n' +
            "    Email: " + email + '\n' +
            "    Password: " + password + '\n' +
            "    Date of Birth: " + dob + '\n' +
            modelsOrderedStr +
            orderListStr;
    }

    public void setName(String name) {
        try (Connection conn = DriverManager.getConnection(Database.DB_URL, Database.USER, Database.PASS);
        PreparedStatement stmt = conn.prepareStatement("UPDATE User SET name = ? WHERE userID = ?")) {
            stmt.setString(1, name);
            stmt.setInt(2, userID);
            stmt.executeUpdate();
            System.out.println("Name changed to " + name);
        } catch (SQLException e) {
            System.out.println("Error while updating user name");
            e.printStackTrace();
        }
    }

    public void setEmail(String email) {
        try (Connection conn = DriverManager.getConnection(Database.DB_URL, Database.USER, Database.PASS);
        PreparedStatement stmt = conn.prepareStatement("UPDATE User SET email = ? WHERE userID = ?")) {
            stmt.setString(1, email);
            stmt.setInt(2, userID);
            stmt.executeUpdate();
            System.out.println("Email changed to " + email);
        } catch (SQLException e) {
            System.out.println("Error while updating user email");
            e.printStackTrace();
        }
    }

    public void setPassword(String password) {
        try (Connection conn = DriverManager.getConnection(Database.DB_URL, Database.USER, Database.PASS);
        PreparedStatement stmt = conn.prepareStatement("UPDATE User SET password = ? WHERE userID = ?")) {
            stmt.setString(1, password);
            stmt.setInt(2, userID);
            stmt.executeUpdate();
            System.out.println("Password changed to " + password);
        } catch (SQLException e) {
            System.out.println("Error while updating user password");
            e.printStackTrace();
        }
    }

    public void setDob(String dob) {
        try (Connection conn = DriverManager.getConnection(Database.DB_URL, Database.USER, Database.PASS);
        PreparedStatement stmt = conn.prepareStatement("UPDATE User SET dob = ? WHERE userID = ?")) {
            stmt.setString(1, dob);
            stmt.setInt(2, userID);
            stmt.executeUpdate();
            System.out.println("Date of birth changed to " + dob);
        } catch (SQLException e) {
            System.out.println("Error while updating user date of birth");
            e.printStackTrace();
        }
    }
}