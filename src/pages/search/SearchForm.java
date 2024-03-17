package pages.search;

import main.Main;
import pages.utils.ColumnPage;
import pages.utils.Popup;
import pages.utils.slider.RangeSlider;
import pages.utils.slider.Slider;

import javax.swing.*;

import com.ibm.db2.jcc.am.ad;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import static pages.utils.UISettings.*;

import database.Database;
import pages.utils.SelectBox;

public class SearchForm extends ColumnPage {

    private static JTextField modelIDField = new JTextField();
    private static JComboBox<String> brandBox;
    private static RangeSlider priceSlider;
    private static RangeSlider starsSlider;
    private static Integer modelID;
    private static String brand;
    private static Integer minPrice;
    private static Integer maxPrice;
    private static Integer minStars;
    private static Integer maxStars;
    private static String[][] data;

    public SearchForm() {
        super("Search Models");
    }

    @Override
    protected void populateContent() {
        brandBox = new JComboBox<String>();
        brandBox.setRenderer(new MyComboBoxRenderer());
        SelectBox.populateComboBox(brandBox, "-Select-", "SELECT name FROM BrandPage ORDER BY name", "name");

        priceSlider = new RangeSlider(0, 500);
        starsSlider = new RangeSlider(0, 10);

        JPanel modelIDEntry = createTempFieldPanel("Model ID:","Enter a Model ID...", modelIDField);
        JPanel brandEntry = createBoxPanel("Brand:", false);
        JPanel modelBrandEntry = doubleItemPanel(modelIDEntry, brandEntry);
        JPanel priceEntry = new Slider(priceSlider, "Price:", "$", 0, 5000);
        JPanel starsEntry = new Slider(starsSlider, "Stars:","", 0, 10);
        JButton searchButton = createButton("Search", BUTTON_GREEN, e -> submit());
        JPanel categoryEntry = new SelectBox("Category:", "Select categories...", "SELECT DISTINCT Cname FROM Belongs", "Cname");
        categoryEntry.setPreferredSize(new Dimension(categoryEntry.getPreferredSize().width, 50));

        addBuffer(0.05);
        addComponent(modelBrandEntry, 0.01);
        addBuffer(0.02);
        addComponent(priceEntry, 0.01);
        addBuffer(0.02);
        addComponent(starsEntry, 0.01);
        addBuffer(0.02);
        addComponent(categoryEntry, 0.8);
        addBuffer(0.02);
        addComponent(searchButton, 0.1);
        setPreferredSizeToBuffer(categoryEntry);
        addSideBuffers();
    }

    private JPanel createBoxPanel(String label, boolean required) {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel lbl = new JLabel(label + (required ? "*" : ""));
        lbl.setFont(new Font ("Arial", Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 0, 10);
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(lbl, gbc);

        brandBox.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(brandBox, gbc);

        return panel;
    }

    private JPanel doubleItemPanel(JPanel item1, JPanel item2) {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
    
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 20);
        panel.add(item1, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 20, 0, 0);
        panel.add(item2, gbc);
    
        return panel;
    }

    private void submit() {
        List<String[]> dataList = new ArrayList<>();
        modelID = getModelID();
        brand = getBrand();
        minPrice = getMinPrice();
        maxPrice = getMaxPrice();
        minStars = getMinStars();
        maxStars = getMaxStars();

        StringBuilder sql = new StringBuilder(
            "SELECT Model.modelID, Model.price, BrandPage.name AS brandName, Model.stars, COUNT(Purchased.modelID) AS productsSold " +
            "FROM Model " +
            "JOIN BrandPage ON Model.url = BrandPage.url " +
            "LEFT JOIN Purchased ON Model.modelID = Purchased.modelID " +
            "WHERE 1=1");

        if (minStars != null) sql.append(" AND Model.stars >= ?");
        if (maxStars != null) sql.append(" AND Model.stars <= ?");
        if (brand != null) sql.append(" AND BrandPage.name = ?");
        if (minPrice != null) sql.append(" AND Model.price >= ?");
        if (maxPrice != null) sql.append(" AND Model.price <= ?");
        if (modelID != null) sql.append(" AND Model.modelID = ?");
        sql.append(" GROUP BY Model.modelID, Model.price, BrandPage.name, Model.stars");
        
        try (Connection conn = DriverManager.getConnection(Database.DB_URL, Database.USER, Database.PASS);
        PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            int paramIndex = 1;
            if (minStars != null) stmt.setInt(paramIndex++, minStars);
            if (maxStars != null) stmt.setInt(paramIndex++, maxStars);
            if (brand != null) stmt.setString(paramIndex++, brand);
            if (minPrice != null) stmt.setInt(paramIndex++, minPrice);
            if (maxPrice != null) stmt.setInt(paramIndex++, maxPrice);
            if (modelID != null) stmt.setInt(paramIndex++, modelID);
        

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String stars = rs.getString("stars");
                if (stars == null) {
                    stars = "0";
                }
                String[] row = {
                    rs.getString("modelID"),
                    rs.getString("price"),
                    rs.getString("brandName"),
                    stars,
                    String.valueOf(rs.getInt("productsSold"))
                };
                dataList.add(row);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while retrieving models from the database: " + e.getMessage());
        }

        data = dataList.toArray(new String[0][]);
        printData();

        Main.goNew(new SearchSelect(), "Search Results");
    }


    public static Integer getModelID() {
        String input = modelIDField.getText().trim();
        if (input.equals("Enter a Model ID...") || input.isEmpty()) {
            return null;
        }
        try {
            int modelID = Integer.parseInt(input);
            if (input.length() != 9) {
                throw new NumberFormatException();
            }
            return modelID;
        } catch (NumberFormatException e) {
            Popup.showErr("Invalid Model ID. Please enter a 9-digit integer.");
            return null;
        }
    }
    public static Integer getMinPrice() {
        return priceSlider.getValue();
    }

    public static Integer getMaxPrice() {
        return  priceSlider.getUpperValue();
    }

    public static Integer getMinStars() {
        return starsSlider.getValue();
    }

    public static Integer getMaxStars() {
        return starsSlider.getUpperValue();
    }

    public static String getBrand() {
        String selectedBrand = (String) brandBox.getSelectedItem();
        return "-Select-".equals(selectedBrand) ? null : selectedBrand;
    }

    public static String[][] getData() {
        return data;
    }

    private void printData(){
        System.out.println("Search successful. Parameters:");
        System.out.println("    Model ID: " + modelID);
        System.out.println("    Brand: " + brand);
        System.out.println("    Price: " + minPrice + " - " + maxPrice);
        System.out.println("    Stars: " + minStars + " - " + maxStars);
        System.out.println("Data:");
        for (String[] row : data) {
            System.out.println("    " + Arrays.toString(row));
        }
    }  
    
    class MyComboBoxRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (isSelected) {
                setBackground(list.isSelectionEmpty() ? DEFAULT_BACKGROUND : DEFAULT_BACKGROUND.darker());
            }
            return this;
        }
    }
}