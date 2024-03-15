package pages;

import java.sql.*;
import javax.swing.*;

import database.Database;
import main.Main;

public class ReviewForm extends ColumnPage{
    private static JTextArea messageField;
    private static JTextField ratingField;

    public ReviewForm() {
        super("Submit your review");
    }

    @Override
    protected void populateContent() {

        createFieldPanel("Message:", false, ratingField);
        createFieldPanel("Rating:", true, messageField );
        addComponent(ratingField, 0.01);
        addBuffer(0.02);
        addComponent(messageField, 0.1);
        addBuffer(0.02);
        createButton("Submit", BUTTON_GREEN, e -> submit());
        addBuffer();
    }

    private void submit() {
        String message = messageField.getText();
        String rating = ratingField.getText();
        int userID = Main.user.getUserID();
        int modelID = ReviewSelect.getSelected();

        try (Connection conn = DriverManager.getConnection(Database.DB_URL, Database.USER, Database.PASS);
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO Review (userID, modelID, rating, message) VALUES (?, ?, ?, ?)")) {

            stmt.setInt(1, userID);
            stmt.setInt(2, modelID);
            stmt.setString(3, rating);
            stmt.setString(4, message);
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(null, "Review submitted successfully!");

        } catch (SQLException e) {
            System.out.println("Error while submitting review");
            e.printStackTrace();
        }
        Main.go("Home");
    }
    
}
