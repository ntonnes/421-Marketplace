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

    // Method to add non-breaking spaces to a string, fixes a problem with grid formatting
    public static String space(int n) {
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) {
            sb.append("\u00A0");
        }
        return sb.toString();
    }

    // Method to create a button; with overloaded methods to simplify default button creation
    public static JButton createButton(String text, ActionListener action, Font font, Color foreground, Color background) {
        JButton button = new JButton(text);
        button.addActionListener(action);
        button.setFont(font);
        button.setForeground(foreground);
        button.setBackground(background);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        return button;
    }
    public static JButton createButton(String text, ActionListener action, Font font) {
        return createButton(text, action, font, Color.WHITE, new Color(0, 123, 255));
    }
    public static JButton createButton(String text, ActionListener action) {
        return createButton(text, action, new Font("Arial", Font.BOLD, 16));
    }

    // Method to show an error message popup
    public static void showErr(String message) {
        UIManager.put("OptionPane.messageForeground", Color.WHITE);
        String htmlMessage = "<html><body><p style='padding: 10px;'>" + message + "</p></body></html>";
        JOptionPane.showMessageDialog(Main.getFrame(), htmlMessage, "Error", JOptionPane.ERROR_MESSAGE);
    }

}