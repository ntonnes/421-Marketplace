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
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> Page.goBack());
        styleButton(backButton, new Color(171, 178, 191),80,10); // Lighter gray

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
        titleLabel.setForeground(new Color(171, 178, 191)); // Light gray
        panel.add(titleLabel, BorderLayout.CENTER); // Add to the center

        update();
    }

    public void update() {
        // Remove the old accountContent
        if (accountContent != null) {
            panel.remove(accountContent);
        }

        // Create a new accountContent based on the user's state
        User user = Main.user;
        if (user instanceof Customer) {
            accountContent = new JLabel("Welcome, " + ((Customer) user).name);
        } else {
            accountContent = createAccountContent();
        }

        // Add the new accountContent to the panel
        panel.add(accountContent, BorderLayout.EAST); // Add to the right

        // Refresh the panel
        panel.revalidate();
        panel.repaint();
    }

    private JPanel createAccountContent() {
        JPanel accountContent = new JPanel();
        accountContent.setLayout(new FlowLayout(FlowLayout.RIGHT));
        accountContent.setBackground(new Color(40, 44, 52)); // Set the background color to match the banner

        JButton loginButton = new JButton("Log in");
        loginButton.addActionListener(e -> Page.goPage(new Login()));
        styleButton(loginButton, new Color(0, 123, 255),100,40); // Pleasant green

        JButton signUpButton = new JButton("Sign up");
        signUpButton.addActionListener(e -> Page.goPage(new Signup()));
        styleButton(signUpButton, new Color(76, 175, 80),100,40); // White

        accountContent.add(loginButton);
        accountContent.add(signUpButton);

        return accountContent;
    }

    private void styleButton(JButton button, Color backgroundColor, int x, int y) {
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE); // Light gray
        button.setBorderPainted(true); // Border painted
        button.setFocusPainted(false);
        button.setFont(new Font("Tahoma", Font.BOLD, 12));
        button.setBorder(BorderFactory.createRaisedBevelBorder()); // Raised bevel border for 3D effect
        button.setPreferredSize(new Dimension(x, y)); // Set preferred size
    }
}