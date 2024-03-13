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
    private static GridBagConstraints gbc;

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
        frame.setLayout(new GridBagLayout());
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
        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.weighty = .05;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTH;
        frame.add(banner, gbc);

        gbc.weighty = .95;
        gbc.gridy = 1;
        frame.getContentPane().add(mainMenu, gbc);
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
        if (newPage.isInstance(getLastPage())) {
            goBack();
            return;
        }
        frame.getContentPane().remove(page);
        frame.getContentPane().add(newPage, gbc);
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
        frame.getContentPane().add(lastPage, gbc);
        Main.page = lastPage;
        frame.revalidate();
        frame.repaint();
    }

    public static Page getLastPage() {
        return page.getLastPage();
    }
}
