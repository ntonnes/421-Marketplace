package database.users;

import java.sql.PreparedStatement;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import database.Database;

public class Member extends Customer {
    private int points;
    private String expDate;

    public Member(Customer customer) {
        super(customer.getUserID(), customer.getName(), customer.getEmail(), customer.getPassword(), customer.getDob());
        this.points = 0;
        this.expDate = LocalDate.now().plusYears(1).format(DateTimeFormatter.ISO_DATE);

        try (Connection conn = DriverManager.getConnection(Database.DB_URL, Database.USER, Database.PASS);
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO Member (userID, expDate, points) VALUES (?, ?, ?)")) {

            stmt.setInt(1, getUserID());
            stmt.setString(2, expDate);
            stmt.setInt(3, points);
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            System.out.println("Error while inserting member into the database");
            e.printStackTrace();
        }
    }


    
    public int getPoints() {
        return points;
    }
    public String getExpDate() {
        return expDate;
    }
}