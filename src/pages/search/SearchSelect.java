package pages.search;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.GridBagLayout;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Comparator;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import database.Database;
import static pages.utils.UISettings.*;

import main.Main;
import pages.utils.ColumnPage;
import pages.utils.Popup;

public class SearchSelect extends ColumnPage {
    private JTable table;
    private JComboBox<String> sortByBox;

    private Integer minStars;
    private Integer maxStars;
    private String brand;
    private Integer minPrice;
    private Integer maxPrice;
    private Integer modelID;
    private String categories;
    private String[][] data;
    private int paramCol = 0;

    public SearchSelect() {
        super("Search Results");
    }

    class ButtonRenderer extends JButton implements TableCellRenderer {
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private String label;
        private boolean isPushed;
        private int row;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }

        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            isPushed = true;
            this.row = row;
            return button;
        }

        public Object getCellEditorValue() {
            if (isPushed) {
                int modelID = (int) table.getModel().getValueAt(row, 0);
                if (label.equals("Add to Cart")) {
                    addToCart(modelID);
                } else if (label.equals("View")) {
                    Main.goNew(new ModelPage(modelID), "Model");
                }
            }
            isPushed = false;
            return label;
        }
    

        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
    }

    @Override
    protected void populateContent() {
        setWeights(1,.3);

        // Create the "sort by" dropdown menu
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
        JPanel sortByPanel = new JPanel();
        sortByPanel.add(new JLabel("Sort by:"));
        sortByPanel.add(sortByBox);

        addComponent(sortByPanel, 0.1);

                
        data = SearchForm.getData();
        minStars = SearchForm.getMinStars();
        maxStars = SearchForm.getMaxStars();
        brand = SearchForm.getBrand();
        minPrice = SearchForm.getMinPrice();
        maxPrice = SearchForm.getMaxPrice();
        modelID = SearchForm.getModelID();

        String[] columnNames = {"Model ID", "Price", "Brand", "Rating"};

        table = new JTable(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            @Override
            public void changeSelection(int rowIndex, int columnIndex, boolean toggle, boolean extend) {
            }
        };

        table.setRowHeight(30);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(table);
        addComponent(scrollPane, 0.7);

        JPanel paramFrame = new JPanel(new BorderLayout());
        addComponent(paramFrame, 0.1);
        JPanel searchParametersPanel = new JPanel(new GridBagLayout());
        paramFrame.add(searchParametersPanel, BorderLayout.WEST);

        addParameter(searchParametersPanel, "Minimum Rating: ", minStars, minStars != 0);
        addParameter(searchParametersPanel, "Maximum Rating: ", maxStars, maxStars != 10);
        addParameter(searchParametersPanel, "Brand", brand, brand != null);
        addParameter(searchParametersPanel, "Minimum Price: $", minPrice, minPrice != 0);
        addParameter(searchParametersPanel, "Maximum Price: $", maxPrice, maxPrice != 500);
        addParameter(searchParametersPanel, "Model ID: ", modelID, modelID != null);

        addBuffer();

        resetWeights();
    }

    private void addParameter(JPanel panel, String name, Object value, boolean condition) {
        if (condition) {
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = paramCol;
            gbc.gridy = 0;
            gbc.weightx = 0;

            JLabel nameLabel = new JLabel(name);
            nameLabel.setForeground(DEFAULT_FOREGROUND);
            panel.add(nameLabel, gbc);

            paramCol++;
            gbc.gridx = paramCol;
            gbc.insets = new Insets(0, 0, 0, 10);
            JLabel valueLabel = new JLabel(value.toString());
            valueLabel.setForeground(BUTTON_GREEN);
            panel.add(valueLabel, gbc);
            paramCol++;
        }
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