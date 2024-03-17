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
    private JComboBox<String> comboBox;
    private Vector<String> options = new Vector<>();
    private JPanel selectedPanel;

    public SelectBox(String label, String placeholder, String query, String column) {
        super(new GridBagLayout());
        setOptions(placeholder, query, column);

        GridBagConstraints gbc = createGridBagConstraints();
        add(createNameLabel(label), gbc);

        comboBox = createComboBox(gbc);
        add(comboBox, gbc);

        add(createBufferPanel(), createBufferGridBagConstraints());

        selectedPanel = createSelectedPanel();
        add(selectedPanel, createSelectedPanelGridBagConstraints());
    }

    private GridBagConstraints createGridBagConstraints() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.insets = new Insets(0, 0, 0, 10);
        return gbc;
    }

    private JLabel createNameLabel(String label) {
        JLabel nameLabel = new JLabel(label);
        nameLabel.setFont(new Font ("Arial", Font.BOLD, 20));
        nameLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
        return nameLabel;
    }

    private JComboBox<String> createComboBox(GridBagConstraints gbc) {
        JComboBox<String> comboBox = new JComboBox<>(options);
        comboBox.setFont(new Font("Arial", Font.PLAIN, 16));
        comboBox.addActionListener(e -> {
            String selectedOption = (String) comboBox.getSelectedItem();
            if (selectedOption != null) {
                options.remove(selectedOption);
                comboBox.removeItem(selectedOption);
                addToSelected(selectedOption, gbc);
            }
        });
        gbc.gridy = 0;
        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        return comboBox;
    }

    private JPanel createBufferPanel() {
        JPanel buffer = new JPanel();
        buffer.setPreferredSize(new Dimension(getPreferredSize().width, 20));
        return buffer;
    }

    private GridBagConstraints createBufferGridBagConstraints() {
        GridBagConstraints bufferGBC = new GridBagConstraints();
        bufferGBC.gridy = 1;
        bufferGBC.gridx = 0;
        bufferGBC.weightx = 0;
        bufferGBC.fill = GridBagConstraints.HORIZONTAL;
        return bufferGBC;
    }

    private JPanel createSelectedPanel() {
        JPanel selectedPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        selectedPanel.setOpaque(false);
        selectedPanel.setBorder(BorderFactory.createLineBorder(BUTTON_RED));
        return selectedPanel;
    }

    private JPanel createGroupedPanel() {
        JPanel groupedPanel = new JPanel();
        groupedPanel.setLayout(new BoxLayout(groupedPanel, BoxLayout.Y_AXIS));
        groupedPanel.setOpaque(false);
        return groupedPanel;
    }

    private GridBagConstraints createSelectedPanelGridBagConstraints() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        return gbc;
    }

    private void addToSelected(String option, GridBagConstraints parentGBC) {
        JPanel panel = createGroupedPanel();  // Use createGroupedPanel to create the container panel
        panel.setOpaque(false);

        JPanel groupedPanel = new JPanel();
        groupedPanel.setLayout(new BoxLayout(groupedPanel, BoxLayout.X_AXIS));
        groupedPanel.setBorder(BorderFactory.createCompoundBorder(
            createCustomBorder(),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));  // Add padding and custom border

        JLabel label = createLabel(option);
        groupedPanel.add(label);
        groupedPanel.add(Box.createRigidArea(new Dimension(10, 0)));  // Add space between label and removeLabel
        JLabel removeLabel = createRemoveLabel(panel, option);
        groupedPanel.add(removeLabel);

        panel.add(groupedPanel, BorderLayout.CENTER);

        parentGBC.gridx++;
        selectedPanel.add(panel, parentGBC);

        revalidate();
        repaint();
    }

    private JLabel createLabel(String option) {
        JLabel label = new JLabel(option);
        label.setForeground(DEFAULT_FOREGROUND);
        label.setBackground(new Color (0,0,0,0));
        label.setOpaque(true);
        label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return label;
    }

    private JLabel createRemoveLabel(JPanel panel, String option) {
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
                selectedPanel.remove(panel);
                options.add(option);
                comboBox.addItem(option);
                revalidate();
                repaint();
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

    public static void populateComboBox(JComboBox<String> box, String placeholder, String query, String column) {
        try (Connection conn = DriverManager.getConnection(Database.DB_URL, Database.USER, Database.PASS);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            box.addItem(placeholder);
            while (rs.next()) {
                box.addItem(rs.getString(column));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while accessing database: " + e.getMessage());
        }
    }

    public void setOptions(String placeholder, String query, String column) {
        try (Connection conn = DriverManager.getConnection(Database.DB_URL, Database.USER, Database.PASS);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                options.add(rs.getString(column));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while accessing the database: " + e.getMessage());
        }
    }

    public Vector<String> getSelectedOptions() {
        Vector<String> selectedOptions = new Vector<>();
        for (Component component : getComponents()) {
            if (component instanceof JPanel) {
                JPanel panel = (JPanel) component;
                for (Component innerComponent : panel.getComponents()) {
                    if (innerComponent instanceof JLabel) {
                        JLabel label = (JLabel) innerComponent;
                        selectedOptions.add(label.getText());
                    }
                }
            }
        }
        return selectedOptions;
    }
}
