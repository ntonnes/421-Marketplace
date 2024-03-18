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
    // Inputs to the constructor
    private final String label;
    private final String query;
    private final String column;
    private final Boolean multi;

    // Represents an option in the comboBox
    private class Option {
        private final String name;
        private boolean selected;
        public Option(String option) {
            this.name = option;
            this.selected = false;
        }
        public String getName() {
            return this.name;
        }
        public boolean isSelected() {
            return this.selected;
        }
        public void select() {
            this.selected = true;
        }
        public void deselect() {
            this.selected = false;
        }
    }

    // The comboBox to hold the options
    private final JComboBox<Option> comboBox = new JComboBox<Option>();
    private final Vector<Option> optionList = new Vector<Option>();
    private final Option placeholderOption;
    private final JPanel selectedPanel = new JPanel(new FlowLayout(FlowLayout.LEFT)); 

    public SelectBox(Boolean multiSelect, String lbl, String placehldr, String qry, String col) {
        super(new GridBagLayout());
        this.label = lbl;
        this.placeholderOption = new Option(placehldr);
        this.query = qry;
        this.column = col;
        this.multi = multiSelect;

        // Set the options for the comboBox
        setOptions();

        // Add a label to the SelectBox
        createNameLabel();

        // Add and fill the comboBox in the SelectBox
        createComboBox();
        
        // Add a buffer panel to create space between the comboBox and the selectedPanel
        // Add the panel to holding the selected options
        if (multi) {
            createBufferPanel();
            createSelectedPanel();
        }
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

    private void refreshComboBox() {
        comboBox.removeAllItems();
        for (Option option : optionList) {
            if (!option.isSelected()){
                comboBox.addItem(option);
            }
        }
    }

    // Create the comboBox and add it to the SelectBox
    private void createComboBox() {
        // Set the comboBox properties
        comboBox.setFont(new Font("Arial", Font.PLAIN, 16));
        comboBox.setRenderer(new CustomRenderer());

        // Add the placeholder option to the comboBox
        comboBox.insertItemAt(placeholderOption, 0);
        comboBox.setSelectedItem(placeholderOption);

        // Add an action listener to the comboBox
        comboBox.addActionListener(e -> {
            Option selectedOption = (Option) comboBox.getSelectedItem();
            if (selectedOption != null && !(selectedOption.getName()).equals(placeholderOption.getName())) {
                if (multi) {
                    selectedOption.select();
                    refreshComboBox();
                    addToPanel(selectedOption);
                } else {
                    for (Option option : optionList) {
                        option.deselect();
                    }
                    selectedOption.select();
                    refreshComboBox();
                }
            }
        });

        // Add the comboBox to the SelectBox
        GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridy = 0;
            gbc.gridx = 1;
            gbc.weightx = 0;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.anchor = GridBagConstraints.LINE_START;
            gbc.insets = new Insets(0, 0, 0, 0);
        add(comboBox, gbc);
    }

    private class CustomRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Option) {
                Option option = (Option) value;
                setText(option.getName());
            }
            return this;
        }
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
        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(this.query)) {

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Option option = new Option(rs.getString(this.column));
                // Add options to both the list and the comboBox
                optionList.add(option);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while accessing database: " + e.getMessage());
        }
    }

    private void addToPanel(Option option) {
        GroupedIcon groupedIcon = new GroupedIcon(option);
        selectedPanel.add(groupedIcon); 
        this.revalidate();
        this.repaint();
    }

    private void removeFromPanel(GroupedIcon groupedIcon) {
        selectedPanel.remove(groupedIcon);
        comboBox.revalidate();
        comboBox.repaint();
    }


    // A private class representing the grouped icon
    private class GroupedIcon extends JPanel {
        private Option option;

        public GroupedIcon(Option opt) {
            super();
            this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
            this.setBorder(BorderFactory.createCompoundBorder(
                createCustomBorder(),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
            ));
            this.option = opt;

            createLabel();
            addSpacer();
            createRemoveLabel();
            
        }

        private void addSpacer() {
            this.add(Box.createRigidArea(new Dimension(10, 0)));
        }

        private void createLabel() {
            JLabel label = new JLabel(option.getName());
            label.setForeground(DEFAULT_FOREGROUND);
            label.setBackground(new Color (0,0,0,0));
            label.setOpaque(true);
            label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            this.add(label);
        }

        private void createRemoveLabel() {
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
                    option.deselect();
                    refreshComboBox();
                    removeFromPanel(GroupedIcon.this);
                }
            });
            this.add(removeLabel);
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
    }
    
    public String[] getSelected() {
        return optionList.stream()
            .filter(Option::isSelected)
            .map(Option::getName)
            .toArray(String[]::new);
    }
}
