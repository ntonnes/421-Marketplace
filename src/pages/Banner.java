package pages;

import javax.swing.*;

import main.Main;

import java.awt.*;

import users.*;


public class Banner {
    public JPanel panel;
    private JComponent accountContent;

    public Banner(String title) {
        this.panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.DARK_GRAY);

        // Create the title label with large font
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 30));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        panel.add(titleLabel, BorderLayout.CENTER);

        // Create the back button
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> Page.goPrevPage());
        panel.add(backButton, BorderLayout.WEST);

        update();  
    }

    public void update() {
        // Remove the old accountContent
        panel.remove(accountContent);

        // Create a new accountContent based on the user's state
        User user = Main.user;
        if (user instanceof Customer) {
            accountContent = new JLabel("Welcome, " + ((Customer) user).name);
        } else {
            accountContent = new JPanel();
            JButton loginButton = new JButton("Log in");
            JButton signUpButton = new JButton("Sign up");
            // Add action listeners to the buttons
            loginButton.addActionListener(e -> {
                Login.showLoginPanel();
                update();
            });
            signUpButton.addActionListener(e -> Signup.showSignupPanel());
            accountContent.add(loginButton);
            accountContent.add(signUpButton);
        }

        // Add the new accountContent to the panel
        panel.add(accountContent, BorderLayout.EAST);

        // Refresh the panel
        panel.revalidate();
        panel.repaint();
    }
}