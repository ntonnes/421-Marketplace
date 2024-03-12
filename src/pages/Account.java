package pages;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import database.Database;
import users.Customer;
import users.Member;

public class Account {
    public Member makeMember(Customer customer) {
        // Create a new Member based on the Customer
        Member member = new Member(customer);

        // Insert the new Member into the database
        try (
             PreparedStatement stmt = Database.db.prepareStatement("INSERT INTO Member (userID, expDate, points) VALUES (?, ?, ?)")) {

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
}
