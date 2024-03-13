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
    public static Banner banner;
    public static Page mainMenu;
    public static Page page;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        // Setting defaults for the GUI
        UIManager.put("Panel.background", Color.DARK_GRAY);
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
        mainMenu = new Menu();
        page = mainMenu;
        banner = new Banner("421 Marketplace");
        frame.getContentPane().add(banner, BorderLayout.NORTH);
        frame.getContentPane().add(mainMenu, BorderLayout.CENTER);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void setUser(User u) {
        user = u;
        banner.updateBanner();
    }

    public static Page getPage() { return page; }

    public static JFrame getFrame() { return frame; }

    public static void goPage(Page newPage) {
        if (newPage == page) {
            return;
        }
        frame.getContentPane().remove(page);
        frame.getContentPane().add(newPage, BorderLayout.CENTER);
        Main.page = newPage;
        frame.revalidate();
        frame.repaint();
    }

    public static void goBack() {
        Page lastPage = page.getLastPage();
        if (lastPage == null){
            return;
        }
        frame.getContentPane().remove(page);
        frame.getContentPane().add(lastPage, BorderLayout.CENTER);
        Main.page = lastPage;
        frame.revalidate();
        frame.repaint();
    }

    public static Page getLastPage() {
        return page.getLastPage();
    }
}
