package pages;

import java.sql.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import database.Database;
import database.Model;
import java.awt.*;
import main.Main;

public class SearchSelect extends ColumnPage {
    private JTable table;

    private double minStars;
    private double maxStars;
    private String brand;
    private double minPrice;
    private double maxPrice;
    private int modelID;
    private String[][] data;

    public SearchSelect(double minStars, double maxStars, String brand, double minPrice, double maxPrice, int modelID) {
        super("Search and Select");
        this.minStars = minStars;
        this.maxStars = maxStars;
        this.brand = brand;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.modelID = modelID;
    }

    @Override
    protected void populateContent() {
        setWeights(.8,.1);
        String[] columnNames = {"Model ID", "Price", "Brand", "Stars"};

        try (Connection conn = DriverManager.getConnection(Database.DB_URL, Database.USER, Database.PASS);) {
            data = Model.getModelSearch(conn, minStars, maxStars, brand, minPrice, maxPrice, modelID);

        } catch (SQLException e) {
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

        // Display the search parameters
        String searchParameters = "Min Stars: " + minStars + ", Max Stars: " + maxStars + ", Brand: " + brand + ", Min Price: " + minPrice + ", Max Price: " + maxPrice + ", Model ID: " + modelID;
        JLabel searchParametersLabel = new JLabel(searchParameters);
        addComponent(searchParametersLabel, 0.1);

        addBuffer(0.05);
        JButton selectButton = createButton("Select", BUTTON_GREEN, e -> select());
        addComponent(selectButton, 0.1);
        addBuffer();

        resetWeights();
    }

    private void select() {
        int row = table.getSelectedRow();
        if (row == -1) {
            Popup.showErr("Please select a model");
        } else {
            String modelIDStr = (String) table.getValueAt(row, 0);
            try {
                modelID = Integer.parseInt(modelIDStr);
                Main.goNew(new ModelPage(modelID), "Model Page");
            } catch (NumberFormatException e) {
                Popup.showErr("Invalid model ID: " + modelIDStr);
            }
        }
    }
}