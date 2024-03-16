package pages;

import main.Main;
import pages.slider.RangeSlider;

import javax.swing.*;

import java.sql.*;
import database.Database;
import java.awt.*;

public class SearchForm extends ColumnPage {

    private static JTextField modelIDField = new JTextField(20);
    private static JComboBox<String> brandBox = new JComboBox<String>();
    private RangeSlider priceSlider;
    private RangeSlider starsSlider;

    public SearchForm() {
        super("Search Models");
        populateBrandBox();
    }

    @Override
    protected void populateContent() {
        JPanel modelIDEntry = createTempFieldPanel("Enter a Model ID...", modelIDField);
        JPanel brandEntry = createBoxPanel("Brand:", false, brandBox);
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

    private JPanel createBoxPanel(String label, boolean required, JComboBox<String> box) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        JLabel lbl = new JLabel(label + (required ? "*" : ""));
        lbl.setFont(Page.FONT_LABEL);
        lbl.setForeground(Color.WHITE);
        lbl.setOpaque(true);
        lbl.setBackground(Color.DARK_GRAY);
        panel.add(lbl);
        panel.add(Box.createRigidArea(new Dimension(10, 0)));
        box.setForeground(Color.WHITE);
        box.setOpaque(true);
        box.setBackground(Color.DARK_GRAY);
        panel.add(box);
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
        lbl.setFont(Page.FONT_LABEL);
        lbl.setForeground(Color.WHITE);
        lbl.setOpaque(true);
        lbl.setBackground(Color.DARK_GRAY);
        panel.add(lbl);
        panel.add(Box.createRigidArea(new Dimension(10, 0)));

        JPanel labelsPanel = new JPanel(new BorderLayout());

        rangeSlider = new RangeSlider(min, max);
        rangeSlider.setValue(min);
        rangeSlider.setUpperValue(max);

        JLabel minLabel = new JLabel("Min: " + rangeSlider.getValue());
        JLabel maxLabel = new JLabel("Max: " + rangeSlider.getUpperValue());
        minLabel.setForeground(Color.WHITE);
        minLabel.setOpaque(true);
        minLabel.setBackground(Color.DARK_GRAY);
        maxLabel.setForeground(Color.WHITE);
        maxLabel.setOpaque(true);
        maxLabel.setBackground(Color.DARK_GRAY);
        labelsPanel.add(minLabel, BorderLayout.WEST);
        labelsPanel.add(maxLabel, BorderLayout.EAST);

        final RangeSlider finalRangeSlider = rangeSlider;
        finalRangeSlider.setForeground(Color.WHITE);
        finalRangeSlider.setOpaque(true);
        finalRangeSlider.setBackground(Color.DARK_GRAY);

        JPanel sliderPanel = new JPanel(new BorderLayout());
        sliderPanel.add(labelsPanel, BorderLayout.NORTH);
        sliderPanel.add(finalRangeSlider, BorderLayout.CENTER);

        finalRangeSlider.addChangeListener(e -> {
            minLabel.setText("Min: " + finalRangeSlider.getValue());
            maxLabel.setText("Max: " + finalRangeSlider.getUpperValue());
        });

        panel.add(sliderPanel);

        return panel;
    }

    private void submit() {
        String placeholder = "Enter a Model ID...";
        Integer modelID = modelIDField.getText().isEmpty() || modelIDField.getText().equals(placeholder) ? null : Integer.parseInt(modelIDField.getText());
        String brand = brandBox.getSelectedItem().equals("-Select-") ? null : (String) brandBox.getSelectedItem();
        Double minPrice = (double) priceSlider.getValue();
        Double maxPrice = (double) priceSlider.getUpperValue();
        Double minStars = (double) starsSlider.getValue();
        Double maxStars = (double) starsSlider.getUpperValue();

        Main.goNew(new SearchSelect(minStars, maxStars, brand, minPrice, maxPrice, modelID), "Search Results");
    }
}