package pages.review;

import java.sql.*;
import javax.swing.*;

import database.Database;
import main.Main;
import pages.utils.ColumnPage;
import static pages.utils.UISettings.*;
import pages.utils.Popup;

public class ReviewForm extends ColumnPage{
    private static JTextPane messageField = new JTextPane();
    private static JTextField ratingField = new JTextField(20);
    private static JButton submitButton;

    public ReviewForm() {
        super("Submit your review");
    }

    @Override
    protected void populateContent() {

        JPanel ratingEntry = createFieldPanel("Rating (0-10):", true, ratingField);
        JPanel messageEntry = createFieldPanel("Message:", false, messageField);
        addComponent(ratingEntry, 0);
        addBuffer(0.02);
        addComponent(messageEntry, 0.3);
        addBuffer(0.02);
        submitButton = createButton("Submit", BUTTON_GREEN, e -> submit());
        addComponent(submitButton, 0.05);
        addBuffer(0.6);
        addSideBuffers();
    }

    private void submit() {
        String message = messageField.getText();
        if (message.isEmpty()) {
            message = null;
        }
        String ratingStr = ratingField.getText();
        int userID = Main.user.getUserID();
        int modelID = ReviewSelect.getSelected();

        // Validate rating
        int rating;
        try {
            rating = Integer.parseInt(ratingStr);
            if (rating < 0 || rating > 10) {
                Popup.showErr("Rating must be an integer between 0 and 10.");
                return;
            }
        } catch (NumberFormatException e) {
            Popup.showErr("Invalid rating. Rating must be an integer between 0 and 10.");
            return;
        }

        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO Review (userID, modelID, rating, message) VALUES (?, ?, ?, ?)")) {

            stmt.setInt(1, userID);
            stmt.setInt(2, modelID);
            stmt.setInt(3, rating);
            if (message == null) {
                stmt.setNull(4, Types.VARCHAR);
            } else {
                stmt.setString(4, message);
            }
            stmt.executeUpdate();

            updateRating(modelID, conn);

            JOptionPane.showMessageDialog(null, "Review submitted successfully!");

        } catch (SQLException e) {
            System.out.println("Error while submitting review");
            e.printStackTrace();
        }
        Main.go("Menu");
    }

    private void updateRating(int modelID, Connection conn) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement("SELECT AVG(rating) FROM Review WHERE modelID = ?")) {
            stmt.setInt(1, modelID);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            double newRating = rs.getDouble(1);

            try (PreparedStatement updateStmt = conn.prepareStatement("UPDATE Model SET stars = ? WHERE modelID = ?")) {
                updateStmt.setDouble(1, newRating);
                updateStmt.setInt(2, modelID);
                updateStmt.executeUpdate();
                System.out.println("Rating for model " + modelID + " updated.");
            }
        }
    }
    
}
