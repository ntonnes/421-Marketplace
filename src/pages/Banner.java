package pages;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import database.users.Customer;
import main.Main;


public class Banner extends JPanel{
    private JPanel navPanel, titlePanel, variablePanel;
    private JButton backButton, homeButton, loginButton, signupButton, logoutButton, accountButton;
    private Color COLOR_BACKGROUND = new Color(40, 44, 52);


    public Banner(String title) {
        super(new GridBagLayout()); 
        this.setBackground(COLOR_BACKGROUND);


        // Create the navigation panel
        navPanel = new JPanel(new GridBagLayout());
        navPanel.setBackground(COLOR_BACKGROUND);
        GridBagConstraints navGBC = createGBC(
            0, 0, 
            GridBagConstraints.BOTH, 
            0.2, 0.0, 
            new Insets(10, 30, 10, 0));
        this.add(navPanel, navGBC);

        backButton = createButton("Back", Page.BUTTON_GRAY, e -> Main.goBack());
        GridBagConstraints backGBC = createGBC(
            0, 0, 
            GridBagConstraints.BOTH, 
            0.5, 1.0, 
            new Insets(10, 0, 10, 5));
        navPanel.add(backButton, backGBC);

        homeButton = createButton("Home", Page.BUTTON_GRAY, e -> Main.go("Menu"));
        GridBagConstraints homeGBC = createGBC(1, 0, 
        GridBagConstraints.BOTH, 
        0.5, 1.0, 
        new Insets(10, 5, 10, 0));
        navPanel.add(homeButton, homeGBC);


        // Create the title panel
        titlePanel = new JPanel(new GridBagLayout());
        titlePanel.setBackground(COLOR_BACKGROUND);
        GridBagConstraints titleGBC = createGBC(
            1, 0, 
            GridBagConstraints.BOTH, 
            0.6, 0.0, 
            new Insets(10, 0, 10, 0));
        this.add(titlePanel, titleGBC);

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
            titleLabel.setFont(new Font("Serif", Font.BOLD, 40));
            titleLabel.setForeground(new Color(190, 199, 208));
        titlePanel.add(titleLabel);

        variablePanel = new JPanel(new GridBagLayout());
        variablePanel.setBackground(COLOR_BACKGROUND);
        variablePanel.setPreferredSize(navPanel.getPreferredSize());
        GridBagConstraints variableGBC = createGBC(
            2, 0, 
            GridBagConstraints.BOTH, 
            0.2, 0.0, 
            new Insets(10, 0, 10, 30));
        this.add(variablePanel, variableGBC);

        if (!(Main.user instanceof Customer)){
            
            loginButton = createButton("Log In", Page.BUTTON_BLUE, e -> Main.go("Login"));
            GridBagConstraints loginGBC = createGBC(
                0, 0, 
                GridBagConstraints.BOTH, 
                0.5, 1.0, 
                new Insets(10, 0, 10, 5));
            variablePanel.add(loginButton, loginGBC);
            
            signupButton = createButton("Sign Up", Page.BUTTON_GREEN, e -> Main.go("Signup"));
            GridBagConstraints signupGBC = createGBC(
                1, 0, 
                GridBagConstraints.BOTH, 
                0.5, 1.0, 
                new Insets(10, 5, 10, 0));
            variablePanel.add(signupButton, signupGBC);
            } else {
                Customer customer = (Customer) Main.user;
                String firstName = customer.getName().split(" ")[0]; // Get the first name
                JLabel messageLabel = new JLabel("Welcome, "+ firstName + "!", SwingConstants.CENTER);
                messageLabel.setFont(Page.FONT_LABEL);
                GridBagConstraints messageGBC = createGBC(
                    0, 0, 
                    GridBagConstraints.BOTH, 
                    1.0, 0.5, 
                    new Insets(0, 0, 0, 0)
                );
                messageGBC.gridwidth = 2;
                messageGBC.anchor = GridBagConstraints.PAGE_END;
            variablePanel.add(messageLabel, messageGBC);
    
            accountButton = createButton("My Cart", Page.BUTTON_BLUE, e -> Main.goNew(new CartSelect(), "Cart"));
            GridBagConstraints accountGBC = createGBC(
                0, 1, 
                GridBagConstraints.BOTH, 
                0.5, 0.5, 
                new Insets(10, 10, 0, 5)
            );
            accountGBC.anchor = GridBagConstraints.NORTHEAST;
            variablePanel.add(accountButton, accountGBC);
        
            logoutButton = createButton("Logout", Page.BUTTON_RED, e -> {customer.logout(); Main.go("Menu"); });
            GridBagConstraints logoutGBC = createGBC(
                1, 1, 
                GridBagConstraints.BOTH, 
                0.5, 0.5, 
                new Insets(10, 5, 0, 10)
            );
                logoutGBC.anchor = GridBagConstraints.NORTHWEST;
            variablePanel.add(logoutButton, logoutGBC);
        }

        this.revalidate();
        this.repaint();
    }



    private JButton createButton(String text, Color color, ActionListener action){
        JButton btn = new JButton(text);
        btn.addActionListener(action);
        btn.setFont(Page.FONT_LABEL);
        btn.setForeground(Page.DEFAULT_FOREGROUND);
        btn.setBackground(color);
        btn.setBorder(Page.BUTTON_RAISED);
        btn.setBorderPainted(true);
        btn.setFocusPainted(false);
        return btn;
    }

    private GridBagConstraints createGBC(int column, int row, int fill, double weightx, double weighty, Insets insets) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = column;
        gbc.gridy = row;
        gbc.fill = fill;
        gbc.weightx = weightx;
        gbc.weighty = weighty;
        gbc.insets = insets;
        return gbc;
    }
}