package pages.search;

import main.Main;
import pages.utils.ColumnPage;
import pages.utils.Popup;
import pages.utils.slider.RangeSlider;
import pages.utils.slider.Slider;
import pages.utils.SelectBox;
import static pages.utils.UISettings.*;
import database.Database;

import javax.swing.*;
import java.awt.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.sql.*;



public class SearchForm extends ColumnPage {

    // Search parameters
    private static Integer modelID;
    private static String[] brands;
    private static Integer minPrice;
    private static Integer maxPrice;
    private static Integer minStars;
    private static Integer maxStars;
    private static String[] categories;
    private static String[][] data;

    private static JTextField modelIDField = new JTextField();
    private static RangeSlider priceSlider = new RangeSlider(0, 500);
    private static RangeSlider starsSlider = new RangeSlider(0, 10);

    private static JComboBox<String> sortByBox;
    private JPanel modelIDPanel;
    private static SelectBox brandPanel;
    private JPanel pricePanel;
    private JPanel starsPanel;
    private static SelectBox categoryPanel;
    private JButton searchButton;

    public SearchForm() {
        super("Search Models");
    }

    @Override
    protected void populateContent() {
        modelIDPanel = createTempFieldPanel(
            "Model ID:","Enter a Model ID...", 
            modelIDField
        );

        brandPanel = new SelectBox(
            true, "Select brands to filter for:", 
            "SELECT DISTINCT name FROM BrandPage", "name"
        );

        pricePanel = new Slider(
            priceSlider, 
            "Price:", "$", 0, 5000
        );

        starsPanel = new Slider(
            starsSlider,
            "Stars:","", 0, 10
        );

        categoryPanel = new SelectBox(
            true, "Select categories to filter for:", 
            "SELECT DISTINCT Cname FROM Belongs", "Cname"
        );

        searchButton = createButton("Search", BUTTON_GREEN, e -> submit());

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
        JPanel sortByPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = createGBC(
            0, 0, 
            GridBagConstraints.BOTH,
            0, 1,
            new Insets(0, 0, 0, 5));
        gbc.anchor = GridBagConstraints.PAGE_END;
        JLabel label = new JLabel("Sort by:");
        label.setFont(new Font ("Arial", Font.BOLD, 20));
        sortByPanel.add(label, gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.insets = new Insets(0, 5, 0, 0);
        sortByPanel.add(sortByBox, gbc);

        JPanel dp1 = doublePanel(modelIDPanel, sortByPanel);
        JPanel dp2 = doublePanel(pricePanel, starsPanel);

        setWeights(0.70, 0.15);

        addBuffer(0);
        addComponent(dp1,0);
        addBuffer(0);
        addComponent(dp2,0);
        addBuffer(0);
        addComponent(brandPanel, 0.48);
        addComponent(categoryPanel, 0.48);
        addComponent(searchButton, 0.04);
        addSideBuffers();

        resetWeights();
    }

    private void submit() {
        List<String[]> dataList = new ArrayList<>();
        modelID = getModelID();
        brands = getBrands();
        minPrice = getMinPrice();
        maxPrice = getMaxPrice();
        minStars = getMinStars();
        maxStars = getMaxStars();
        categories = getCategories();

        StringBuilder sql = new StringBuilder(
            "SELECT Model.modelID, Model.price, BrandPage.name AS brand, Model.stars, COUNT(Purchased.modelID) AS productsSold " +
            "FROM Model " +
            "JOIN BrandPage ON Model.url = BrandPage.url " +
            "LEFT JOIN Purchased ON Model.modelID = Purchased.modelID " +
            "WHERE 1=1");

        if (minStars != null) sql.append(" AND Model.stars >= ?");
        if (maxStars != null) sql.append(" AND Model.stars <= ?");
        if (brands != null && brands.length > 0) {
            String placeholders = String.join(", ", Collections.nCopies(brands.length, "?"));
            sql.append(" AND BrandPage.name IN (" + placeholders + ")");
        }
        if (minPrice != null) sql.append(" AND Model.price >= ?");
        if (maxPrice != null) sql.append(" AND Model.price <= ?");
        if (modelID != null) sql.append(" AND Model.modelID = ?");
        if (categories != null && categories.length > 0) {
            String placeholders = String.join(" UNION ", Collections.nCopies(categories.length, "SELECT modelID FROM Belongs WHERE Cname = ?"));
            sql.append(" AND Model.modelID IN (" + placeholders + ")");
        }
        sql.append(" GROUP BY Model.modelID, Model.price, BrandPage.name, Model.stars");
        sql.append(" ").append(getSortSQL());
        
        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            int paramIndex = 1;
            if (minStars != null) stmt.setInt(paramIndex++, minStars);
            if (maxStars != null) stmt.setInt(paramIndex++, maxStars);
            if (brands != null) {
                for (String brand : brands) {
                    stmt.setString(paramIndex++, brand);
                }
            }
            if (minPrice != null) stmt.setInt(paramIndex++, minPrice);
            if (maxPrice != null) stmt.setInt(paramIndex++, maxPrice);
            if (modelID != null) stmt.setInt(paramIndex++, modelID);
            if (categories != null) {
                for (String category : categories) {
                    stmt.setString(paramIndex++, category);
                }
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String stars = rs.getString("stars");
                if (stars == null) {
                    stars = "0";
                }
                String[] row = {
                    rs.getString("modelID"),
                    rs.getString("price"),
                    rs.getString("brand"),
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

        Main.goNew(new SearchSelect((String) sortByBox.getSelectedItem()), "Search Results");
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

    public static String[] getBrands() {
        return brandPanel.getSelected();
    }

    public static String[] getCategories() {
        return categoryPanel.getSelected();
    }

    public static String[][] getData() {
        return data;
    }
    public static String getSortSQL() {
        if (sortByBox.getSelectedItem().equals("Best Selling")) {
            return "ORDER BY productsSold DESC";
        } else if (sortByBox.getSelectedItem().equals("Price Low to High")) {
            return "ORDER BY Model.price";
        } else if (sortByBox.getSelectedItem().equals("Price High to Low")) {
            return "ORDER BY Model.price DESC";
        } else if (sortByBox.getSelectedItem().equals("Rating High to Low")) {
            return "ORDER BY Model.stars DESC";
        } else if (sortByBox.getSelectedItem().equals("Rating Low to High")) {
            return "ORDER BY Model.stars";
        } else if (sortByBox.getSelectedItem().equals("Brand")) {
            return "ORDER BY BrandPage.name";
        }
        return "ORDER BY productsSold DESC";
    }

    private void printData(){
        System.out.println("Search successful. Parameters:");
        
        System.out.println("    Model ID: " + modelID);
        System.out.println("    Price: " + minPrice + " - " + maxPrice);
        System.out.println("    Stars: $" + minStars + " - $" + maxStars);
        System.out.println("    Brands: " + Arrays.toString(brands));
        System.out.println("    Categories: " + Arrays.toString(categories));
        System.out.println("\nData:");
        for (String[] row : data) {
            System.out.println("    " + Arrays.toString(row));
        }
    }

}