package main;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import pages.Menu;
import database.users.*;
import pages.*;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Main {
    private static JFrame frame;
    public static User user;
    public static Banner banner = new Banner("421 Marketplace");
    public static Page currentPage = new Menu();

    public static void setUser(User u) {
        user = u;
        banner.updateBanner();
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
        frame.setSize(1200,800);
        frame.setMinimumSize(new Dimension(800, 600));

        // Change the default close operation
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Delete the user from the database if they are a guest
                if (!(user instanceof Customer)){
                    user.delete();
                }
                // Disconnect from the database and exit the application
                System.exit(0);
            }
        });

        user = new User();


        currentPage.previousPage = currentPage;
        frame.getContentPane().add(banner.panel, BorderLayout.NORTH);
        frame.getContentPane().add(currentPage.panel, BorderLayout.CENTER);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
