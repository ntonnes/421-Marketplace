package pages.review;

import java.util.List;
import java.sql.*;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import database.Customer;
import database.Database;
import main.Main;
import pages.utils.ColumnPage;
import pages.utils.Popup;
import static pages.utils.UISettings.*;

public class ReviewSelect extends ColumnPage{
    private JTable table;
    private static int modelID;
    String[][] data;

    public ReviewSelect() {
        super("Select a model to review");
    }

    @Override
    protected void populateContent() {
        setWeights(.8,.1);
        Customer customer = (Customer)(Main.user);
        String[] columnNames = {"Model ID", "Price", "Brand", "Stars"};
        Integer[] unreviewed;

        try (Connection conn = Database.connect()) {
            int[] models = getModelsOrdered(customer.getEmail(), conn);
            unreviewed = filterReviewed(models, conn);
            data = new String[unreviewed.length][4];
            for (int i = 0; i < unreviewed.length; i++) {

                int modelID = unreviewed[i];

                data[i][0] = String.valueOf(modelID);
                data[i][1] = "$" + String.valueOf(getPrice(modelID, conn));
                data[i][2] = getBrand(modelID, conn);
                data[i][3] = String.valueOf(getRating(modelID, conn));
            }
        } catch (SQLException e) {
            System.out.println("Error while accessing the database");
            e.printStackTrace();
        }

        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(30);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(table);
        addComponent(scrollPane, 0.7);

        addBuffer(0.05);
        JButton selectButton = createButton("Select", BUTTON_GREEN, e -> select());
        addComponent(selectButton, 0.1);
        addBuffer();

        resetWeights();
    }

    private void select() {
        int row = table.getSelectedRow();
        if (row == -1) {
            Popup.showErr("Please select a model to review");
        } else {
            String modelIDStr = (String) table.getValueAt(row, 0);
            try {
                modelID = Integer.parseInt(modelIDStr);
                Main.goNew(new ReviewForm(), "Review Form");
            } catch (NumberFormatException e) {
                Popup.showErr("Invalid model ID: " + modelIDStr);
            }
        }
    }

   public static int getSelected() {
        return modelID;
    }

    private Integer[] filterReviewed(int[] models, Connection conn) throws SQLException {
        List<Integer> unreviewedModels = new ArrayList<>();
        for (int model : models) {
            try (PreparedStatement stmt = conn.prepareStatement(
                "SELECT COUNT(*) FROM Review WHERE userID = ? AND modelID = ?")) {
                stmt.setInt(1, Main.user.getUserID());
                stmt.setInt(2, model);
                ResultSet rs = stmt.executeQuery();
                if (rs.next() && rs.getInt(1) == 0) {
                    unreviewedModels.add(model);
                }
            }
        }
        return unreviewedModels.toArray(new Integer[0]);
    }

    private int[] getModelsOrdered(String email, Connection conn) {
        List<Integer> modelIDs = new ArrayList<>();

        try {
            try (PreparedStatement stmt = conn.prepareStatement(
                    "SELECT DISTINCT P.ModelID " +
                    "FROM Purchased P " +
                    "JOIN Order O ON P.OrderID = O.OrderID " +
                    "WHERE O.Email = ?")) {
                stmt.setString(1, email);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    modelIDs.add(rs.getInt("ModelID"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error while retrieving ordered models");
            e.printStackTrace();
        }
        return modelIDs.stream().mapToInt(Integer::intValue).toArray();
    }

    private double getPrice(int modelID, Connection conn) {
        try (PreparedStatement stmt = conn.prepareStatement(
                "SELECT Price FROM Model WHERE ModelID = ?")) {
            stmt.setInt(1, modelID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("Price");
            } else {
                throw new RuntimeException("Model not found: " + modelID);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while retrieving model price: " + e.getMessage());
        }
    }

    private String getBrand(int modelID, Connection conn) {
        try (PreparedStatement stmt = conn.prepareStatement(
                "SELECT BrandPage.Name FROM Model " +
                "JOIN BrandPage ON Model.URL = BrandPage.URL " +
                "WHERE ModelID = ?")) {
            stmt.setInt(1, modelID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("Name");
            } else {
                throw new RuntimeException("Model not found: " + modelID);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while retrieving model brand: " + e.getMessage());
        }
    }
    
    private int getRating(int modelID, Connection conn) {
        try (PreparedStatement stmt = conn.prepareStatement(
                "SELECT AVG(rating) FROM Review WHERE ModelID = ?")) {
            stmt.setInt(1, modelID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int stars = rs.getInt(1);
                try (PreparedStatement stmt2 = conn.prepareStatement(
                    "UPDATE Model SET stars = ? WHERE ModelID = ?")) {
                    stmt2.setInt(1, stars);
                    stmt2.setInt(2, modelID);
                    stmt2.executeUpdate();
                }
                return stars;
            } else {
                throw new RuntimeException("Model not found: " + modelID);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while retrieving model rating: " + e.getMessage());
        }
    }
}