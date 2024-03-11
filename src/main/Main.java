package main;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import pages.Menu;
import users.*;
import database.*;
import pages.*;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;

public class Main {
    private static JFrame frame;
    public static Connection db = Database.db;
    public static User user;
    public static Banner banner = new Banner("421 Marketplace");
    public static Page currentPage = new Menu();

    public static void setUser(User u) {
        user = u;
        banner.update();
    }

    public static JFrame getFrame() { return frame; }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        // Setting defaults for the GUI
        UIManager.put("Panel.background", new Color(64, 64, 64));
        UIManager.put("Button.background", Color.LIGHT_GRAY);
        UIManager.put("Button.foreground", Color.BLACK);
        UIManager.put("Label.foreground", Color.WHITE);

        // Initialize the application window
        frame = new JFrame("421 Marketplace");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000,800);

        // Change the default close operation
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Delete the user from the database if they are a guest
                if (!(user instanceof Customer)){
                    Database.deleteUser(user);
                }
                // Disconnect from the database and exit the application
                Database.disconnect(db);
                System.exit(0);
            }
        });

        user = User.newGuest();


        currentPage.previousPage = currentPage;
        frame.getContentPane().add(banner.panel, BorderLayout.NORTH);
        frame.getContentPane().add(currentPage.panel, BorderLayout.CENTER);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
