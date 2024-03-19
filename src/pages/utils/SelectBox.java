package pages.utils;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;

import database.Database;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.util.ArrayList;
import java.util.Vector;

import static pages.utils.UISettings.*;

public class SelectBox extends JPanel {
    // Inputs to the constructor
    private final String label;
    private final String query;
    private final String column;

    private final Vector<String> optionList = new Vector<String>();
    private final JPanel selectedPanel = new JPanel(new FlowLayout(FlowLayout.LEFT)); 

    public SelectBox(Boolean multiSelect, String lbl, String placehldr, String qry, String col) {
        super(new GridBagLayout());
        this.label = lbl;
        this.query = qry;
        this.column = col;
        selectedPanel.setPreferredSize(new Dimension(getWidth(), selectedPanel.getPreferredSize().height));

        // Set the options for the comboBox
        setOptions();

        // Add a label to the SelectBox
        createNameLabel();
        createSelectedPanel();
    }

    // Create a label and add it to the SelectBox
    private void createNameLabel() {
        JLabel nameLabel = new JLabel(this.label);
        nameLabel.setFont(new Font ("Arial", Font.BOLD, 20));
        nameLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
        GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.weightx = 0;
            gbc.weighty = 0;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.anchor = GridBagConstraints.LINE_START;
            gbc.insets = new Insets(0, 0, 0, 10);
        add(nameLabel, gbc);
    }

    // Create the panel to hold the selected options and add it to the SelectBox
    // Create the panel to hold the selected options and add it to the SelectBox
    private void createSelectedPanel() {
        selectedPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 1;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        add(selectedPanel, gbc);  // Add the scrollPane to the SelectBox

        for (String option : optionList) {
            addToPanel(option);
        }

        this.revalidate();
        this.repaint();
    }
    
    // Fills a comboBox with the results of a query
    public void setOptions() {
        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(this.query)) {

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                optionList.add(rs.getString(this.column));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while accessing database: " + e.getMessage());
        }
    }

    private void addToPanel(String option) {
        GroupedIcon groupedIcon = new GroupedIcon(option);
        selectedPanel.add(groupedIcon); 

    }


    // A private class representing the grouped icon
    private class GroupedIcon extends JPanel {
        private final String name;
        private final JLabel label;
        private Boolean selected = true;

        public GroupedIcon(String opt) {
            this.name = opt;
            this.label = new JLabel(name);
            createLabel();
            this.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    selected = !selected;
                    if (selected) {
                        label.setForeground(DEFAULT_FOREGROUND); 
                    } else {
                        label.setForeground(Color.LIGHT_GRAY.darker()); 
                    }
                    repaint();
                }
            });
        }

        private void createLabel() {
            label.setBackground(new Color (0,0,0,0));
            label.setOpaque(true);
            label.setFont(new Font("Arial", Font.PLAIN, 14));
            this.setBorder(BorderFactory.createCompoundBorder(
                createCustomBorder(),
                BorderFactory.createEmptyBorder(3,6,3,6)
            ));
            this.add(label);
        }

        private Border createCustomBorder() {
            return new AbstractBorder() {
                @Override
                public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
                    int borderThickness = 2;
                    int halfThickness = 1;
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setStroke(new BasicStroke(borderThickness));
                    g2d.setColor(DEFAULT_FOREGROUND.darker());
                    g2d.drawRoundRect(borderThickness + halfThickness, borderThickness + halfThickness, width - 2 * borderThickness, height - 2 * borderThickness, height - 2 * borderThickness, height - 2 * borderThickness);
                    if (selected) { 
                        g2d.setColor(BUTTON_BLUE.darker());
                    } else {
                        g2d.setColor(Color.DARK_GRAY);
                    }
                    g2d.fillRoundRect(borderThickness, borderThickness, width - 2 * borderThickness, height - 2 * borderThickness, height - 2 * borderThickness, height - 2 * borderThickness);
                    g2d.setColor(DEFAULT_FOREGROUND);
                    g2d.drawRoundRect(borderThickness, borderThickness, width - 2 * borderThickness, height - 2 * borderThickness, height - 2 * borderThickness, height - 2 * borderThickness);
                    g2d.dispose();
                }
            };
        }
    }
    
    public String[] getSelected(){
        ArrayList<String> selected = new ArrayList<>();
        for (Component c : selectedPanel.getComponents()) {
            if (c instanceof GroupedIcon) {
                GroupedIcon icon = (GroupedIcon) c;
                if (icon.selected) {
                    selected.add(icon.name);
                }
            }
        }
        return selected.toArray(new String[0]);
    }
}
