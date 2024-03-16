package pages;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.border.EmptyBorder;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import database.Database;
import main.Main;

public class CartSelect extends ColumnPage {
    private JTable table;
    private JLabel totalCostLabel;

    public CartSelect() {
        super("Your Cart");
    }

    @Override
    protected void populateContent() {
        setWeights(.8,.1);
        String[] columnNames = {"Model ID", "Price", "Brand", "Rating", "Quantity"};

        List<String[]> data = new ArrayList<>();
        double totalCost = 0.0;
        try (Connection conn = DriverManager.getConnection(Database.DB_URL, Database.USER, Database.PASS);
             PreparedStatement stmt = conn.prepareStatement(
                "SELECT Model.modelID, Model.price, BrandPage.name AS brand, Model.stars, InCart.copies "+
                "FROM Model INNER JOIN InCart ON Model.modelID = InCart.modelID "+
                "INNER JOIN BrandPage ON Model.url = BrandPage.url "+
                "WHERE InCart.userID = ?")) {
            stmt.setInt(1, Main.user.getUserID());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String[] row = new String[5];
                row[0] = String.valueOf(rs.getInt("modelID"));
                row[1] = "$" + String.valueOf(rs.getDouble("price"));
                row[2] = rs.getString("brand");
                row[3] = String.valueOf(rs.getDouble("stars"));
                row[4] = String.valueOf(rs.getInt("copies"));
                data.add(row);
                totalCost += rs.getDouble("price") * rs.getInt("copies");
            }
        } catch (SQLException e) {
            System.out.println("Error while accessing the database");
            e.printStackTrace();
        }


        DefaultTableModel model = new DefaultTableModel(data.toArray(new String[0][]), columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(30);
        table.setShowGrid(true);
        table.setGridColor(Color.WHITE);
        table.setRowMargin(5);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setForeground(Color.WHITE);
        table.setBackground(Color.DARK_GRAY);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JTableHeader header = table.getTableHeader();
        header.setBackground(Color.DARK_GRAY);
        header.setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false); 
        scrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));
        addComponent(scrollPane, 0.7);

        totalCostLabel = new JLabel("Total Cost: $" + String.format("%.2f", totalCost));
        addComponent(totalCostLabel, 0);

        addBuffer(0.05);

        JPanel buttonPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 10, 0, 10);
        gbc.weightx = 0.5;
        gbc.weighty = 1;

        JButton orderButton = createButton("Place Order", BUTTON_GREEN, e -> order());
        gbc.gridx = 0;
        buttonPanel.add(orderButton, gbc);
        gbc.insets = new Insets(0, 10, 0, 10);

        JButton removeButton = createButton("Remove Item", BUTTON_RED, e -> removeItem());
        gbc.gridx = 1;
        buttonPanel.add(removeButton, gbc);

        addComponent(buttonPanel, 0.1);
        addBuffer();

        resetWeights();
    }

    private void order() {
        // Implement the order logic here
    }

    private void removeItem() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select a model to remove.");
            return;
        }

        String modelID = (String) table.getValueAt(selectedRow, 0);
        int copiesInCart = Integer.parseInt((String) table.getValueAt(selectedRow, 4));

        String input = JOptionPane.showInputDialog(null, "Enter the number of copies to remove (max " + copiesInCart + "):");
        int copiesToRemove;
        try {
            copiesToRemove = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid input. Please enter a number.");
            return;
        }

        if (copiesToRemove < 1 || copiesToRemove > copiesInCart) {
            JOptionPane.showMessageDialog(null, "Invalid number of copies. Please enter a number between 1 and " + copiesInCart + ".");
            return;
        }

        try (Connection conn = DriverManager.getConnection(Database.DB_URL, Database.USER, Database.PASS)) {
            if (copiesToRemove == copiesInCart) {
                // If all copies are removed, delete the row from the database
                try (PreparedStatement stmt = conn.prepareStatement(
                    "DELETE FROM InCart WHERE modelID = ? AND userID = ?")) {
                    stmt.setString(1, modelID);
                    stmt.setInt(2, Main.user.getUserID());
                    stmt.executeUpdate();
                }
            } else {
                // If not all copies are removed, update the number of copies in the database
                try (PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE InCart SET copies = copies - ? WHERE modelID = ? AND userID = ?")) {
                    stmt.setInt(1, copiesToRemove);
                    stmt.setString(2, modelID);
                    stmt.setInt(3, Main.user.getUserID());
                    stmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            System.out.println("Error while accessing the database");
            e.printStackTrace();
        }

        Main.goBack();
        Main.goNew(new CartSelect(), "Your Cart");
    }
}