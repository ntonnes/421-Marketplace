package pages;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.*;

import database.Database;
import database.users.Customer;
import database.users.Member;

public class Account extends Page{
    public Account(String name) {
        super(name);
        //TODO Auto-generated constructor stub
    }

    public Member makeMember(Customer customer) {
        // Create a new Member based on the Customer
        Member member = new Member(customer);

        // Insert the new Member into the database
        try (Connection conn = DriverManager.getConnection(Database.DB_URL, Database.USER, Database.PASS);
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO Member (userID, expDate, points) VALUES (?, ?, ?)")) {

            stmt.setInt(1, member.getUserID());
            stmt.setString(2, member.getExpDate());
            stmt.setInt(3, member.getPoints());
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error while inserting member into the database");
            e.printStackTrace();
        }

        return member;
    }

    @Override
    protected void populateContent() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'populateContent'");
    }

}
