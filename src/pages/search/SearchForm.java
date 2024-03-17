package pages.search;

import main.Main;
import pages.slider.RangeSlider;
import pages.utils.ColumnPage;
import pages.utils.Popup;

import javax.swing.*;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.BorderLayout;
import java.util.List;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;

import database.Database;

public class SearchForm extends ColumnPage {

    private static JTextField modelIDField = new JTextField(20);
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
        populateBrandBox();

        priceSlider = new RangeSlider(0, 5000);
        starsSlider = new RangeSlider(0, 10);

        JPanel modelIDEntry = createTempFieldPanel("Enter a Model ID...", modelIDField);
        JPanel brandEntry = createBoxPanel("Brand:", false);
        JPanel priceEntry = createRangeSliderPanel(priceSlider, "Price:", 0, 5000);
        JPanel starsEntry = createRangeSliderPanel(starsSlider, "Stars:", 0, 10);
        JButton searchButton = createButton("Search", BUTTON_GREEN, e -> submit());

        addBuffer(0.05);
        addComponent(modelIDEntry, 0.01);
        addBuffer(0.02);
        addComponent(brandEntry, 0.01);
        addBuffer(0.02);
        addComponent(priceEntry, 0.01);
        addBuffer(0.02);
        addComponent(starsEntry, 0.01);
        addBuffer(0.02);
        addComponent(searchButton, 0.1);
        addBuffer();
    }

    private JPanel createBoxPanel(String label, boolean required) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

        JLabel lbl = new JLabel(label + (required ? "*" : ""));
        lbl.setFont(new Font ("Arial", Font.BOLD, 20));
        panel.add(lbl);
        panel.add(Box.createRigidArea(new Dimension(10, 0)));

        brandBox.setFont(new Font("Arial", Font.PLAIN, 16));
        JLabel sampleLabel = new JLabel("Sample Text");
        sampleLabel.setFont(brandBox.getFont());
        Dimension preferredSize = sampleLabel.getPreferredSize();
        brandBox.setPreferredSize(preferredSize);
        panel.add(brandBox);

        return panel;
    }

    private void populateBrandBox() {
        try (Connection conn = DriverManager.getConnection(Database.DB_URL, Database.USER, Database.PASS);
             PreparedStatement stmt = conn.prepareStatement("SELECT name FROM BrandPage ORDER BY name")) {
            ResultSet rs = stmt.executeQuery();
            brandBox.addItem("-Select-");
            while (rs.next()) {
                brandBox.addItem(rs.getString("name"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while retrieving brands from the database: " + e.getMessage());
        }
    }


    private JPanel createRangeSliderPanel(RangeSlider rangeSlider, String label, int min, int max) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font ("Arial", Font.BOLD, 20));
        lbl.setBorder(BorderFactory.createEmptyBorder(18, 0, 0, 0));
        panel.add(lbl);
        panel.add(Box.createRigidArea(new Dimension(20, 10)));

        JPanel labelsPanel = new JPanel(new BorderLayout());

        rangeSlider.setValue(min);
        rangeSlider.setUpperValue(max);

        JLabel minLabel = new JLabel("Min: " + rangeSlider.getValue());
        minLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        minLabel.setFont(new Font ("Arial", Font.PLAIN, 16));
        labelsPanel.add(minLabel, BorderLayout.WEST);

        JLabel maxLabel = new JLabel("Max: " + rangeSlider.getUpperValue());
        maxLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        maxLabel.setFont(new Font ("Arial", Font.PLAIN, 16));
        labelsPanel.add(maxLabel, BorderLayout.EAST);

        rangeSlider.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        JPanel sliderPanel = new JPanel(new BorderLayout());
        sliderPanel.add(labelsPanel, BorderLayout.NORTH);
        sliderPanel.add(rangeSlider, BorderLayout.CENTER);

        rangeSlider.addChangeListener(e -> {
            minLabel.setText("Min: " + rangeSlider.getValue());
            maxLabel.setText("Max: " + rangeSlider.getUpperValue());
        });

        panel.add(sliderPanel);

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
        return priceSlider.getValue() == priceSlider.getMinimum() ? null : priceSlider.getValue();
    }

    public static Integer getMaxPrice() {
        return priceSlider.getUpperValue() == priceSlider.getMaximum() ? null : priceSlider.getUpperValue();
    }

    public static Integer getMinStars() {
        return starsSlider.getValue() == starsSlider.getMinimum() ? null : starsSlider.getValue();
    }

    public static Integer getMaxStars() {
        return starsSlider.getUpperValue() == starsSlider.getMaximum() ? null : starsSlider.getUpperValue();
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
}