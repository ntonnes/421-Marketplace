package pages;

import database.Database;
import database.Model;
import main.Main;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.swing.*;
import users.Customer;

public class Review extends Form {
    private JList<Model> modelList;
    private JPanel ratingPanel;
    private JSpinner ratingSpinner;
    private JTextArea messageArea;

    public Review() {
        super("Leave a Review");
    }

    @Override
    protected void populateContent() {
        if (!(Main.user instanceof Customer)) {
            Utils.showErr("Please log into a customer account to leave a review");
            goBack();
            return;
        } else {
            Customer customer = (Customer) Main.user;
            modelList = new JList<> (customer.getModelsOrdered());
        }
        
        modelList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        content.add(new JScrollPane(new JScrollPane(modelList)));

        addButton("Review this Item", BUTTON_BLUE, e -> openReviewPanel());
    }

    private void openReviewPanel() {
        Model selectedModel = modelList.getSelectedValue();
        if (selectedModel == null) {
            Utils.showErr("Please select a model.");
            return;
        }

        ratingPanel = new JPanel();
        ratingPanel.setLayout(new BoxLayout(ratingPanel, BoxLayout.Y_AXIS));

        ratingSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 10, 1));
        ratingPanel.add(new JLabel("Rating:"));
        ratingPanel.add(ratingSpinner);

        messageArea = new JTextArea(5, 20);
        ratingPanel.add(new JLabel("Message:"));
        ratingPanel.add(new JScrollPane(messageArea));

        int result = JOptionPane.showConfirmDialog(ratingPanel, "Leave a Review for " + selectedModel.getModelID());
        if (result == JOptionPane.OK_OPTION) {
            submit();
        }
    }

    @Override
    protected void submit() {
        int rating = (Integer) ratingSpinner.getValue();
        String message = messageArea.getText();
        Model selectedModel = modelList.getSelectedValue();
        int userID = Main.user.userID;
        int modelID = selectedModel.getModelID();

        try (
            PreparedStatement stmt = Database.db.prepareStatement("INSERT INTO Review (userID, modelID, rating, message) VALUES (?, ?, ?, ?)")) {

            stmt.setInt(1, userID);
            stmt.setInt(2, modelID);
            stmt.setInt(3, rating);
            stmt.setString(4, message);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Review submitted successfully.");
                goBack();
            } else {
                JOptionPane.showMessageDialog(null, "Failed to submit review.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}