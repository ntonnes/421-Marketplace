package pages;
import java.awt.*;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.*;

import main.Main;

public class Utils {

    public static Font arialB = new Font("Arial", Font.BOLD, 16);
    public static Font arial = new Font("Arial", Font.PLAIN, 14);

    // Method to beautify a text entry field
    public static JTextComponent beautifyField(JTextComponent field, Font font) {
        field.setOpaque(false);
        field.setBorder(new MatteBorder(0, 0, 1, 0, Color.WHITE));
        field.setForeground(Color.WHITE);
        field.setFont(font);
        field.setPreferredSize(new Dimension(field.getPreferredSize().width, 30));
        field.setCaretColor(Color.WHITE);
        return field;
    }

    // Method to create a label to accompany a text entry field
    public static JLabel createLabel(String text, Font font, Boolean required) {
        String asterisk = "";
        if (required) {
            asterisk = "<font color='red'>* </font>";
        }

        JLabel label = new JLabel("<html>" + asterisk + text + "</html>");
        label.setFont(font);
        label.setForeground(Color.WHITE);
        return label;
    }

    // Method to create a button; with overloaded methods to simplify default button creation
    public static JButton createButton(String text, ActionListener action, Font font, Color foreground, Color background) {
        JButton button = new JButton(text);
        button.addActionListener(action);
        button.setFont(font);
        button.setForeground(foreground);
        button.setBackground(background);
        return button;
    }
    public static JButton createButton(String text, ActionListener action, Font font) {
        return createButton(text, action, font, Color.WHITE, new Color(0, 123, 255));
    }
    public static JButton createButton(String text, ActionListener action) {
        return createButton(text, action, new Font("Arial", Font.BOLD, 16));
    }

    public static JButton styleButton(String name, Color color, int x, int y, ActionListener action) {
        JButton button = new JButton(name);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setBorderPainted(true);
        button.setFocusPainted(false);
        button.setFont(new Font("Tahoma", Font.BOLD, 14));
        button.setBorder(BorderFactory.createRaisedBevelBorder());
        button.setPreferredSize(new Dimension(x, y));
        button.addActionListener(action);
        return button;
    }

    // Method to show an error message popup
    public static void showErr(String message) {
        UIManager.put("OptionPane.messageForeground", Color.WHITE);
        String htmlMessage = "<html><body><p style='padding: 10px;'>" + message + "</p></body></html>";
        JOptionPane.showMessageDialog(Main.getFrame(), htmlMessage, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static GridBagConstraints makeGBC(int top, int left, int bottom, int right) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(top, left, bottom, right);
        gbc.ipadx = Main.getFrame().getWidth() / 3;
        return gbc;
    }
}