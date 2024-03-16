package pages;

import java.awt.Color;
import java.awt.Dimension;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import database.Database;


public class ModelPage extends ColumnPage {
    private int modelID;
    private String brand;
    private double price;
    private double stars;
    private String url;
    private String description;
    private String[][] reviews;

    public ModelPage(int modelID) {
        super("Model " + modelID);
    }

    @Override
    protected void populateContent() {
        setWeights(.8, .1);

        
        addComponent(createInfoLabel("Model ID: " + modelID), .1);
        addComponent(new JPanel(), .01);
        addComponent(createInfoLabel("Price: " + price), 0.1);
        addComponent(new JPanel(), .01);
        addComponent(createInfoLabel("Stars: " + stars), 0.1);
        addComponent(new JPanel(), .3);

        addComponent(createInfoLabel("Brand: " + brand), .1);
        addComponent(new JPanel(), .01);
        addComponent(createInfoLabel("Description: " + description), 0.1);
        addComponent(new JPanel(), .01);
        addComponent(createInfoLabel("URL: " + url), 0.1);

        String columnNames[] = {"Rating", "Review"};

        DefaultTableModel model = new DefaultTableModel(reviews, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setRowHeight(30);
        table.setShowGrid(true);
        table.setGridColor(java.awt.Color.WHITE); // Fix: Change the argument type to java.awt.Color
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
    }

    public void findModel(int modelID) {
        reviews = null;

        try (Connection conn = DriverManager.getConnection(Database.DB_URL, Database.USER, Database.PASS)) {
            String modelQuery = "SELECT * FROM Model WHERE modelID = ?";
            PreparedStatement modelStmt = conn.prepareStatement(modelQuery);
            modelStmt.setInt(1, modelID);
            ResultSet modelRs = modelStmt.executeQuery();

            if (modelRs.next()) {
                url = modelRs.getString("url");
                price = modelRs.getDouble("price");
                stars = modelRs.getDouble("stars");

                String brandQuery = "SELECT * FROM BrandPage WHERE url = ?";
                PreparedStatement brandStmt = conn.prepareStatement(brandQuery);
                brandStmt.setString(1, url);
                ResultSet brandRs = brandStmt.executeQuery();

                if (brandRs.next()) {
                    brand = brandRs.getString("name");
                    description = brandRs.getString("description");

                    String reviewQuery = "SELECT rating, message FROM Review WHERE modelID = ?";
                    PreparedStatement reviewStmt = conn.prepareStatement(reviewQuery);
                    reviewStmt.setInt(1, modelID);
                    ResultSet reviewRs = reviewStmt.executeQuery();

                    List<String[]> reviewList = new ArrayList<>();
                    while (reviewRs.next()) {
                        String rating = Integer.toString(reviewRs.getInt("rating"));
                        String message = reviewRs.getString("message");
                        reviewList.add(new String[]{rating, message});
                    }

                    reviews = reviewList.toArray(new String[0][]);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
