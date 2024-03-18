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
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        modelIDPanel = createTempFieldPanel(
            "Model ID:","Enter a Model ID...", 
            modelIDField
        );

        brandPanel = new SelectBox(
            true, "Brand:", "Select a brand...", 
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
            true, "Category:", "Select categories...", 
            "SELECT DISTINCT Cname FROM Belongs", "Cname"
        );

        searchButton = createButton("Search", BUTTON_GREEN, e -> submit());

        this.add(modelIDPanel);
        this.add(pricePanel);
        this.add(starsPanel);
        this.add(brandPanel);
        this.add(new Box.Filler(categoryPanel.getMinimumSize(), categoryPanel.getPreferredSize(), new Dimension(0, Integer.MAX_VALUE)));
        this.add(searchButton);
        //addBuffer(0.05);
        //addComponent(modelBrandPanel, 0.01);
        //addBuffer(0.02);
        //addComponent(pricePanel, 0.01);
        //addBuffer(0.02);
        //addComponent(starsPanel, 0.01);
        //addBuffer(0.02);
        //addComponent(categoryPanel, 0.8);
        //addBuffer(0.02);
        //addComponent(searchButton, 0.1);
        //addSideBuffers();
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
            "SELECT Model.modelID, Model.price, BrandPage.name AS brandName, Model.stars, COUNT(Purchased.modelID) AS productsSold " +
            "FROM Model " +
            "JOIN BrandPage ON Model.url = BrandPage.url " +
            "LEFT JOIN Purchased ON Model.modelID = Purchased.modelID " +
            "WHERE 1=1");

        if (minStars != null) sql.append(" AND Model.stars >= ?");
        if (maxStars != null) sql.append(" AND Model.stars <= ?");
        if (brands != null) {
            sql.append(" AND BrandPage.name IN (");
            for (int i = 0; i < brands.length; i++) {
                sql.append("?");
                if (i < brands.length - 1) {
                    sql.append(", ");
                }
            }
            sql.append(")");
        }
        if (minPrice != null) sql.append(" AND Model.price >= ?");
        if (maxPrice != null) sql.append(" AND Model.price <= ?");
        if (modelID != null) sql.append(" AND Model.modelID = ?");
        if (categories != null) {
            sql.append(" AND Model.modelID IN (");
            for (int i = 0; i < categories.length; i++) {
                sql.append("SELECT modelID FROM Belongs WHERE Cname = ?");
                if (i < categories.length - 1) {
                    sql.append(" UNION ");
                }
            }
            sql.append(")");
        }
        sql.append(" GROUP BY Model.modelID, Model.price, BrandPage.name, Model.stars");
        
        try (Connection conn = DriverManager.getConnection(Database.DB_URL, Database.USER, Database.PASS);
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

    public static String[] getBrands() {
        return brandPanel.getSelected();
    }

    public static String[] getCategories() {
        return categoryPanel.getSelected();
    }

    public static String[][] getData() {
        return data;
    }

    private void printData(){
        System.out.println("Search successful. Parameters:");
        System.out.println("    Model ID: " + modelID);
        System.out.println("    Brand: " + brands);
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