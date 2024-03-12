package pages;

import javax.swing.*;
import java.awt.*;

import main.Main;
import users.*;

public class Banner {
    public JPanel panel;
    private JComponent accountContent;

    public Banner(String title) {
        this.panel = new JPanel();
        panel.setLayout(new BorderLayout(10, 10)); // Use BorderLayout
        panel.setBackground(new Color(40, 44, 52)); // Dark gray
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Padding

        // Create the back button with custom colors and padding
        JButton backButton = Utils.styleButton("Back", new Color(171, 178, 191),80,10, e -> Page.goBack()); // Lighter gray

        // Create a buffer panel and add it to the WEST of the BorderLayout
        JPanel bufferPanel = new JPanel();
        bufferPanel.setPreferredSize(new Dimension(120, 0)); // Set preferred size
        bufferPanel.setOpaque(false); // Make it invisible

        // Create a new JPanel, add the back button and the buffer panel to it, and add it to the WEST region
        JPanel westPanel = new JPanel(new BorderLayout());
        westPanel.setOpaque(false); // Make it invisible
        westPanel.add(backButton, BorderLayout.WEST);
        westPanel.add(bufferPanel, BorderLayout.CENTER);
        panel.add(westPanel, BorderLayout.WEST);

        // Create the title label with large font and white color
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 40));
        titleLabel.setForeground(new Color(190, 199, 208)); // Light gray
        panel.add(titleLabel, BorderLayout.CENTER); // Add to the center

        updateBanner();
    }

    public void updateBanner() {
        // Remove the old accountContent
        if (accountContent != null) {
            panel.remove(accountContent);
        }

        // Call createAccountContent to get the new accountContent
        accountContent = createAccountContent();
    
        // Add the new accountContent to the panel and refresh
        panel.add(accountContent, BorderLayout.EAST);
        panel.revalidate();
        panel.repaint();
    }

    private JPanel createAccountContent() {
        User user = Main.user;
        JPanel accountContent = new JPanel();
    
        if (user instanceof Customer) {
            accountContent.setLayout(new FlowLayout(FlowLayout.LEFT));
            accountContent.setOpaque(false); // Make it invisible
    
            JLabel welcomeLabel = new JLabel("Welcome, " + ((Customer) user).getName());
            welcomeLabel.setFont(new Font("Serif", Font.BOLD, 20)); // Make the text larger
            accountContent.add(welcomeLabel);
    
            JButton logoutButton = Utils.styleButton("Logout", Color.RED, 80, 20, e -> Main.setUser(User.newGuest())); // Small red button
            accountContent.add(logoutButton); // Place the logout button to the right of the welcome label
        } else {
            accountContent.setLayout(new FlowLayout(FlowLayout.RIGHT));
            accountContent.setBackground(new Color(40, 44, 52)); // Set the background color to match the banner
    
            JButton loginButton = Utils.styleButton("Log in", new Color(0, 123, 255),100,40, e -> {
                if (Main.currentPage instanceof Login){
                    return;
                } else if (Main.currentPage.previousPage instanceof Login){
                    Page.goBack();
                } else {
                    Page.goPage(new Login());
                }
            });
    
            JButton signUpButton = Utils.styleButton("Sign up", new Color(76, 175, 80),100,40, e -> {
                if (Main.currentPage instanceof Signup){
                    return;
                } else if (Main.currentPage.previousPage instanceof Signup){
                    Page.goBack();
                } else {
                    Page.goPage(new Signup());
                }
            });
    
            accountContent.add(loginButton);
            accountContent.add(signUpButton);
        }
    
        return accountContent;
    }
}