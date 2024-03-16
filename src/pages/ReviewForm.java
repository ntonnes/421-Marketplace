package pages;

import java.sql.*;
import javax.swing.*;

import database.Database;
import main.Main;
import database.Model;

public class ReviewForm extends ColumnPage{
    private static JTextPane messageField = new JTextPane();
    private static JTextField ratingField = new JTextField(20);
    private static JButton submitButton;

    public ReviewForm() {
        super("Submit your review");
    }

    @Override
    protected void populateContent() {

        JPanel ratingEntry = createFieldPanel("Rating:", true, ratingField);
        JPanel messageEntry = createFieldPanel("Message:", false, messageField);
        addComponent(ratingEntry, 0.01);
        addBuffer(0.02);
        addComponent(messageEntry, 0.1);
        addBuffer(0.02);
        submitButton = createButton("Submit", BUTTON_GREEN, e -> submit());
        addComponent(submitButton, 0.1);
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

            updateRating(modelID, conn);

            JOptionPane.showMessageDialog(null, "Review submitted successfully!");

        } catch (SQLException e) {
            System.out.println("Error while submitting review");
            e.printStackTrace();
        }
        Main.go("Home");
    }

    private void updateRating(int modelID, Connection conn) throws SQLException {
        double oldRating = Model.getModel(modelID, conn).getStars();
        try (PreparedStatement stmt = conn.prepareStatement("SELECT AVG(rating) FROM Review WHERE modelID = ?")) {
            stmt.setInt(1, modelID);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            double newRating = rs.getDouble(1);

            try (PreparedStatement updateStmt = conn.prepareStatement("UPDATE Model SET stars = ? WHERE modelID = ?")) {
                updateStmt.setDouble(1, newRating);
                updateStmt.setInt(2, modelID);
                updateStmt.executeUpdate();
                System.out.println("Rating for model " + modelID + " updated from " + oldRating + " to " + newRating);
            }
        }
    }
    
}
