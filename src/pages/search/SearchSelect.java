package pages.search;

import static pages.utils.UISettings.*;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Comparator;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import database.Database;

import main.Main;
import pages.utils.ColumnPage;
import pages.utils.Popup;

public class SearchSelect extends ColumnPage {
    private JTable table;
    private JComboBox<String> sortByBox;
    private JButton addToCartButton;

    private Integer minStars;
    private Integer maxStars;
    private String[] brands;
    private Integer minPrice;
    private Integer maxPrice;
    private Integer modelID;
    private String[] categories;
    private String[][] data;

    public SearchSelect(String sort) {
        super("Search Results");
    }

    public JPanel createSortByBox() {
        String[] sortByOptions = {
            "Best Selling",
            "Price Low to High",
            "Price High to Low",
            "Rating High to Low",
            "Rating Low to High",
            "Model ID",
            "Brand"
        };
        sortByBox = new JComboBox<>(sortByOptions);
        sortByBox.addActionListener(e -> sortData());
        JLabel sortByLabel = new JLabel("Sort by:");
            sortByLabel.setFont(new Font ("Arial", Font.BOLD, 20));
        JPanel sortByPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.anchor = GridBagConstraints.WEST;
            gbc.fill = GridBagConstraints.HORIZONTAL;
        sortByPanel.add(sortByLabel, gbc);
            gbc.gridx = 1;
            gbc.insets = new Insets(0, 10, 0, 0);
        sortByPanel.add(sortByBox, gbc);
        return sortByPanel;
    }

    @Override
    protected void populateContent() {
        setWeights(.6,.2);

        JPanel sortByPanel = createSortByBox();

        addComponent(sortByPanel, 0);

                
        data = SearchForm.getData();
        minStars = SearchForm.getMinStars();
        maxStars = SearchForm.getMaxStars();
        brands = SearchForm.getBrands();
        minPrice = SearchForm.getMinPrice();
        maxPrice = SearchForm.getMaxPrice();
        modelID = SearchForm.getModelID();
        categories = SearchForm.getCategories();

        String[] columnNames = {"Model ID", "Price", "Brand", "Rating"};

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
        addComponent(scrollPane, 0.6);

        JPanel searchParametersPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addComponent(searchParametersPanel, .3);

        if (minStars != 0) {
            searchParametersPanel.add(new JLabel("Minimum Rating: "));
            searchParametersPanel.add(new JLabel(String.valueOf(minStars)));
        }
        if (maxStars != 10) {
            searchParametersPanel.add(new JLabel("Maximum Rating: "));
            searchParametersPanel.add(new JLabel(String.valueOf(maxStars)));
        }
        if (brands != null && brands.length != 0) {
            searchParametersPanel.add(new JLabel("Brands: " + String.join(", ", brands)));
        }
        if (minPrice != 0 && minPrice != null) {
            searchParametersPanel.add(new JLabel("Minimum Price: $" + String.valueOf(minPrice)));
        }
        if (maxPrice != 500 && maxPrice != null) {
            searchParametersPanel.add(new JLabel("Maximum Price: $" + String.valueOf(maxPrice)));
        }
        if (modelID != null) {
            searchParametersPanel.add(new JLabel("Model ID: " + modelID.toString()));
        }
        if (categories != null && categories.length != 0) {
            searchParametersPanel.add(new JLabel("Categories: " + String.join(", ", categories)));
        }

        addToCartButton = createButton("Add to Cart", BUTTON_GREEN, e -> addToCart(
            Integer.parseInt((String) table.getValueAt(table.getSelectedRow(), 0)
        )));
        addComponent(addToCartButton, 0.1);

        addSideBuffers();
        resetWeights();
    }

    private void sortData() {
        String sortBy = (String) sortByBox.getSelectedItem();
        Comparator<String[]> comparator;
        switch (sortBy) {
            case "Best Selling":
                // Assuming the best selling models have the highest model IDs
                comparator = Comparator.comparingInt(arr -> Integer.parseInt(arr[0]));
                break;
            case "Price Low to High":
                comparator = Comparator.comparingDouble(arr -> Double.parseDouble(arr[1]));
                break;
            case "Price High to Low":
                comparator = Comparator.comparingDouble((String[] arr) -> arr[1] == null || arr[1].isEmpty() ? 0 : Double.parseDouble(arr[1])).reversed();
                break;
            case "Rating High to Low":
                comparator = Comparator.comparingDouble((String[] arr) -> arr[3] == null || arr[3].isEmpty() ? 0 : Double.parseDouble(arr[3])).reversed();
                break;
            case "Rating Low to High":
                comparator = Comparator.comparingDouble(arr -> Integer.parseInt(arr[3]));
                break;
            case "Model ID":
                comparator = Comparator.comparingInt(arr -> Integer.parseInt(arr[0]));
                break;
            case "Brand":
                comparator = Comparator.comparing(arr -> arr[2]);
                break;
            default:
                throw new IllegalArgumentException("Invalid sort by option: " + sortBy);
        }
        Arrays.sort(data, comparator);

        // Update the table with the sorted data
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setDataVector(data, new String[] {"Model ID", "Price", "Brand", "Rating"});
    }

    private void addToCart(int modelID) {
        try (Connection conn = Database.connect();) {
            String sql = "SELECT * FROM Product WHERE modelID = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, modelID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int userID = Main.user.getUserID();

                // Check if the user and model ID pair exists in the InCart table
                sql = "SELECT copies FROM InCart WHERE userID = ? AND modelID = ?";
                stmt = conn.prepareStatement(sql);
                stmt.setInt(1, userID);
                stmt.setInt(2, modelID);
                rs = stmt.executeQuery();

                if (rs.next()) {
                    // If the pair exists, increment the copies column
                    int copies = rs.getInt("copies") + 1;
                    sql = "UPDATE InCart SET copies = ? WHERE userID = ? AND modelID = ?";
                    stmt = conn.prepareStatement(sql);
                    stmt.setInt(1, copies);
                    stmt.setInt(2, userID);
                    stmt.setInt(3, modelID);
                    stmt.executeUpdate();
                    Popup.showMsg("Successfully added model " + modelID + " to your cart. You now have " + copies + " copies in your cart.");
                } else {
                    // If the pair doesn't exist, add a new row with copies = 1
                    sql = "INSERT INTO InCart (userID, modelID, copies) VALUES (?, ?, 1)";
                    stmt = conn.prepareStatement(sql);
                    stmt.setInt(1, userID);
                    stmt.setInt(2, modelID);
                    stmt.executeUpdate();
                    Popup.showMsg("Successfully added model " + modelID + " to your cart. You now have 1 copy in your cart.");
                }
            } else {
                Popup.showErr("This model is out of stock. Try again later." + modelID);
            }
        } catch (SQLException e) {
            Popup.showErr("Error finding product: " + e.getMessage());
        }
    }
}