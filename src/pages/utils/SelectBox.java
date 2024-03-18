package pages.utils;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;

import database.Database;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.util.Vector;

import static pages.utils.UISettings.*;

public class SelectBox extends JPanel {
    private final String label;
    private final String placeholder;
    private final String query;
    private final String column;

    private JComboBox<String> comboBox;
    private Vector<String> options;
    private JPanel selectedPanel;
    private Vector<String> selectedOptions;

    private GridBagConstraints selectedGBC = 
        new GridBagConstraints(
            0, 0, 1, 1, 1, 1, 
            GridBagConstraints.CENTER, 
            GridBagConstraints.BOTH, 
            new Insets(0, 0, 0, 0), 0, 0);


    public SelectBox(String lbl, String placehldr, String qry, String col) {
        super(new GridBagLayout());
        this.label = lbl;
        this.placeholder = placehldr;
        this.query = qry;
        this.column = col;
        this.selectedOptions = new Vector<String>();

        // Set the options for the comboBox
        setOptions();

        // Add a label to the SelectBox
        createNameLabel();

        // Add and fill the comboBox in the SelectBox
        createComboBox();
        
        // Add a buffer panel to create space between the comboBox and the selectedPanel
        createBufferPanel();

        // Add the panel to holding the selected options
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

    // Create the comboBox and add it to the SelectBox
    private void createComboBox() {
        this.comboBox = new JComboBox<>(options);
        comboBox.setFont(new Font("Arial", Font.PLAIN, 16));
        comboBox.insertItemAt(this.placeholder, 0);
        comboBox.setSelectedItem(this.placeholder);
        GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridy = 0;
            gbc.gridx = 1;
            gbc.weightx = 0;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.anchor = GridBagConstraints.LINE_START;
            gbc.insets = new Insets(0, 0, 0, 0);
        comboBox.addActionListener(e -> {
            String selectedOption = (String) comboBox.getSelectedItem();
            if (selectedOption != null && !selectedOption.equals(this.placeholder)) {
                options.remove(selectedOption);
                comboBox.removeItem(selectedOption);
                addToSelected(selectedOption, gbc);
            }
        });
        add(comboBox, gbc);
    }

    // Create a buffer panel to add space between the comboBox and the selectedPanel
    private void createBufferPanel() {
        JPanel buffer = new JPanel();
        buffer.setPreferredSize(new Dimension(getPreferredSize().width, 20));
        GridBagConstraints bufferGBC = new GridBagConstraints();
            bufferGBC.gridy = 1;
            bufferGBC.gridx = 0;
            bufferGBC.weightx = 0;
            bufferGBC.fill = GridBagConstraints.HORIZONTAL;
        add(buffer, bufferGBC);
    }

    // Create the panel to hold the selected options and add it to the SelectBox
    private void createSelectedPanel() {
        JPanel selectedPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        selectedPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridy = 2;
            gbc.gridx = 0;
            gbc.gridwidth = 2;
            gbc.weightx = 1;
            gbc.weighty = 1;
            gbc.fill = GridBagConstraints.BOTH;
        add(selectedPanel, gbc);
    }

    // Fills a comboBox with the results of a query
    public void setOptions() {
        try (Connection conn = DriverManager.getConnection(Database.DB_URL, Database.USER, Database.PASS);
             
             PreparedStatement stmt = conn.prepareStatement(this.query)) {
            ResultSet rs = stmt.executeQuery();
            options = new Vector<>();
            while (rs.next()) {
                // Add options to both the list and the comboBox
                options.add(rs.getString(this.column));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while accessing database: " + e.getMessage());
        }
    }

    private void addToSelected(String option) {
        GroupedIcon groupedIcon = new GroupedIcon(option, parentGBC);
        selectedPanel.add(groupedIcon, parentGBC);
        selectedOptions.add(option); 
        revalidate();
        repaint();
    }

    private void removeFromSelected(GroupedIcon groupedIcon) {
        selectedPanel.remove(groupedIcon.getPanel());
        String option = groupedIcon.getOption();
        selectedOptions.remove(option);
        options.add(option);
        comboBox.addItem(option);
        comboBox.revalidate();
        comboBox.repaint();
    }


    // A private class representing the grouped icon
    private class GroupedIcon extends JPanel {
        private String option;
        private JPanel panel;

        public GroupedIcon(String selected, GridBagConstraints parentGBC) {
            this.option = selected;
            this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
            this.setBorder(BorderFactory.createCompoundBorder(
                createCustomBorder(),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
            ));

            JLabel label = createLabel(option);
            this.add(label);
            this.add(Box.createRigidArea(new Dimension(10, 0)));  // Add space between label and removeLabel
            JLabel removeLabel = createRemoveLabel();
            this.add(removeLabel);

            panel = createGroupedPanel();  // Use createGroupedPanel to create the container panel
            panel.setOpaque(false);
            panel.add(this, BorderLayout.CENTER);

            parentGBC.gridx++;
        }

        private JPanel createGroupedPanel() {
            JPanel groupedPanel = new JPanel();
            groupedPanel.setLayout(new BoxLayout(groupedPanel, BoxLayout.Y_AXIS));
            groupedPanel.setOpaque(false);
            return groupedPanel;
        }

        private JLabel createLabel(String option) {
            JLabel label = new JLabel(option);
            label.setForeground(DEFAULT_FOREGROUND);
            label.setBackground(new Color (0,0,0,0));
            label.setOpaque(true);
            label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            return label;
        }

        private JLabel createRemoveLabel() {
            JLabel removeLabel = new JLabel("X");
            removeLabel.setOpaque(true);
            removeLabel.setForeground(DEFAULT_BACKGROUND.darker());
            removeLabel.setOpaque(false);
            removeLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
            removeLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    removeLabel.setForeground(BUTTON_RED);
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    removeLabel.setForeground(DEFAULT_BACKGROUND.darker());
                }
                @Override
                public void mouseClicked(MouseEvent e) {
                    removeFromSelected(GroupedIcon.this);
                }
            });
            return removeLabel;
        }

        private Border createCustomBorder() {
            return new AbstractBorder() {
                @Override
                public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
                    int borderThickness = 5;
                    int halfThickness = 3;
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setStroke(new BasicStroke(borderThickness));
                    g2d.setColor(DEFAULT_FOREGROUND.darker());
                    g2d.drawRoundRect(borderThickness + halfThickness, borderThickness + halfThickness, width - 2 * borderThickness, height - 2 * borderThickness, height - 2 * borderThickness, height - 2 * borderThickness);
                    g2d.setColor(BUTTON_BLUE.darker());
                    g2d.fillRoundRect(borderThickness, borderThickness, width - 2 * borderThickness, height - 2 * borderThickness, height - 2 * borderThickness, height - 2 * borderThickness);
                    g2d.setColor(DEFAULT_FOREGROUND);
                    g2d.drawRoundRect(borderThickness, borderThickness, width - 2 * borderThickness, height - 2 * borderThickness, height - 2 * borderThickness, height - 2 * borderThickness);
                    g2d.dispose();
                }
            };
        }

        public JPanel getPanel() {
            return panel;
        }

        public String getOption() {
            return option;
        }
    }

}
