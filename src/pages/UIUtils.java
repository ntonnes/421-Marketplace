package pages;
import java.awt.*;

import javax.swing.*;
import javax.swing.text.*;


import main.Main;

public class UIUtils {




    // Method to create a title with overloaded methods
    public static JLabel createTitleLabel(String text, int compass, Font font, Color color) {
        JLabel titleLabel = new JLabel(text, compass);
        titleLabel.setFont(font);
        titleLabel.setForeground(color);
        return titleLabel;
    }




    // Method to show an error message popup
    public static void showErr(String message) {
        UIManager.put("OptionPane.messageForeground", Page.DEFAULT_FOREGROUND);
        String htmlMessage = "<html><body><p style='padding: 10px;'>" + message + "</p></body></html>";
        JOptionPane.showMessageDialog(Main.getFrame(), htmlMessage, "Error", JOptionPane.ERROR_MESSAGE);
    }


    // Method to add a component to a container with gbc
    public static void addToGrid(Container container, Component component, GridBagConstraints gbc) {
        container.add(component, gbc);
    }

    // Method to set GridBagConstraints before adding a component to a container
    public static GridBagConstraints createGBC(int gridx, int gridy, double weightx, double weighty, int fill) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = gridx;
        gbc.gridy = gridy;
        gbc.fill = fill;
        gbc.weightx = weightx;
        gbc.weighty = 0;
        gbc.insets = new Insets(10,10,10,10); // set padding
        return gbc;
    }

}