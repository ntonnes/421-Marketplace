package pages.utils;

import javax.swing.*;

import main.Main;


public class Popup {

    // Method to show an error message popup
    public static void showErr(String message) {
        UIManager.put("OptionPane.messageForeground", Page.DEFAULT_FOREGROUND);
        String htmlMessage = "<html><body><p style='padding: 10px;'>" + message + "</p></body></html>";
        JOptionPane.showMessageDialog(Main.getFrame(), htmlMessage, "Error", JOptionPane.ERROR_MESSAGE);
    }

}