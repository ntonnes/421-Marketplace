package main;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import users.*;
import database.*;
import pages.*;
import java.awt.Color;

public class Main {
    private static JFrame frame;
    public static User user;
    public static Page currentPage = new Menu();
    public static Banner banner = new Banner("421 Marketplace");;

    public static void setUser(User u) {
        user = u;
        banner.update();
    }

    private static int quit() {
        Database.disconnect();
        frame.dispose();
        System.exit(0);
        return 1;
    }

    public static JFrame getFrame() { return frame; }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        UIManager.put("Panel.background", new Color(64, 64, 64));
        UIManager.put("Button.background", Color.LIGHT_GRAY);
        UIManager.put("Button.foreground", Color.BLACK);
        UIManager.put("Label.foreground", Color.WHITE);

        frame = new JFrame("421 Marketplace");
        frame.setDefaultCloseOperation(quit());
        frame.setSize(1000,800);

        Database.connect();
        user = User.newGuest();

        currentPage.previousPage = currentPage;
        frame.getContentPane().add(banner.panel);
        frame.getContentPane().add(currentPage.panel);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
