package pages;

import javax.swing.*;

import database.users.*;

import java.awt.*;
import java.awt.event.ActionListener;

import main.Main;

public class Banner extends JPanel{
    private CardLayout accountStates;
    private JPanel navPanel, titlePanel, variablePanel, guestPanel, customerPanel;
    private JButton backButton, homeButton, loginButton, signupButton, logoutButton, accountButton;
    private Color COLOR_BACKGROUND = new Color(40, 44, 52);


    public Banner(String title) {
        super(new GridBagLayout()); 
        this.setBackground(COLOR_BACKGROUND);


        // Create the navigation panel
        navPanel = new JPanel(new GridBagLayout());
        navPanel.setBackground(COLOR_BACKGROUND);
        GridBagConstraints navGBC = new GridBagConstraints();
            navGBC.gridx = 0; navGBC.gridy = 0;
            navGBC.fill = GridBagConstraints.BOTH;
            navGBC.weightx = 0.10;
            navGBC.insets = new Insets(10, 30, 10, 0);
        this.add(navPanel, navGBC);

        backButton = createButton("Back", UIUtils.BUTTON_GRAY, e -> Main.goBack());
        GridBagConstraints backGBC = new GridBagConstraints();
            backGBC.gridx = 0;
            backGBC.gridy = 0;
            backGBC.fill = GridBagConstraints.BOTH;
            backGBC.weightx = 0.5;
            backGBC.weighty = 1.0;
            backGBC.insets = new Insets(10, 0, 10, 5);
        navPanel.add(backButton, backGBC);

        homeButton = createButton("Home", UIUtils.BUTTON_GRAY, e -> Main.go("Menu"));
        GridBagConstraints homeGBC = new GridBagConstraints();
            homeGBC.gridx = 1;
            homeGBC.gridy = 0;
            homeGBC.fill = GridBagConstraints.BOTH;
            homeGBC.weightx = 0.5;
            homeGBC.weighty = 1.0;
            homeGBC.insets = new Insets(10, 5, 10, 0);
        navPanel.add(homeButton, homeGBC);


        // Create the title panel
        titlePanel = new JPanel(new GridBagLayout());
        titlePanel.setBackground(COLOR_BACKGROUND);
        GridBagConstraints titleGBC = new GridBagConstraints();
            titleGBC.gridx = 1; titleGBC.gridy = 0;
            titleGBC.fill = GridBagConstraints.BOTH;
            titleGBC.weightx = 1;
            titleGBC.insets = new Insets(10, 0, 10, 0);
        this.add(titlePanel, titleGBC);

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
            titleLabel.setFont(new Font("Serif", Font.BOLD, 40));
            titleLabel.setForeground(new Color(190, 199, 208));
        titlePanel.add(titleLabel);

        
        // Create the variable panel, initially set to guest
        accountStates = new CardLayout();
        variablePanel = new JPanel(accountStates);
        variablePanel.setBackground(COLOR_BACKGROUND);
        GridBagConstraints accountGBC = new GridBagConstraints();
            accountGBC.gridx = 2; accountGBC.gridy = 0;
            accountGBC.fill = GridBagConstraints.BOTH;
            accountGBC.weightx = 0.10;
            accountGBC.insets = new Insets(10, 0, 10, 30);
        this.add(variablePanel, accountGBC);
        guestPanel = new JPanel(new GridBagLayout());
        variablePanel.add(guestPanel, "Guest");

        loginButton = createButton("Log In", UIUtils.BUTTON_BLUE, e -> Main.go("Login"));
        GridBagConstraints loginGBC = new GridBagConstraints();
            loginGBC.gridx = 0;
            loginGBC.gridy = 0;
            loginGBC.fill = GridBagConstraints.BOTH;
            loginGBC.weightx = 0.5;
            loginGBC.weighty = 1.0;
            loginGBC.insets = new Insets(10, 0, 10, 5);
        guestPanel.add(loginButton, loginGBC);

        signupButton = createButton("Sign Up", UIUtils.BUTTON_GREEN, e -> Main.go("Signup"));
        GridBagConstraints signupGBC = new GridBagConstraints();
            signupGBC.gridx = 1;
            signupGBC.gridy = 0;
            signupGBC.fill = GridBagConstraints.BOTH;
            signupGBC.weightx = 0.5;
            signupGBC.weighty = 1.0;
            signupGBC.insets = new Insets(10, 5, 10, 0);
        guestPanel.add(signupButton, signupGBC);

        accountStates.show(variablePanel, "Guest");
        this.revalidate();
        this.repaint();
    }


    // Update the banner based on the user's state
    public void updateBanner(User user) {
        if (user instanceof Customer){
            customerPanel = getCustomerPanel((Customer) user);
            variablePanel.add(customerPanel, "Customer");
            accountStates.show(variablePanel, "Customer");
        }
        else if (customerPanel != null) {
            variablePanel.remove(customerPanel);
            accountStates.show(variablePanel, "Guest");
        }
        else {
            accountStates.show(variablePanel, "Guest");
        }
        this.revalidate();
        this.repaint();
    }


    // Create a new customer panel
    private JPanel getCustomerPanel(Customer customer) {
        JPanel customerPanel = new JPanel(new GridBagLayout());

        JLabel messageLabel = new JLabel("Welcome, "+ customer.getName()+ "!", SwingConstants.CENTER);
        messageLabel.setFont(UIUtils.FONT_LABEL);
        GridBagConstraints messageGBC = new GridBagConstraints();
            messageGBC.gridx = 0;
            messageGBC.gridy = 0;
            messageGBC.gridwidth = 2;
            messageGBC.fill = GridBagConstraints.BOTH;
            messageGBC.weightx = 1.0;
            messageGBC.weighty = 0.5;
            messageGBC.anchor = GridBagConstraints.PAGE_END;
            messageGBC.insets = new Insets(0, 0, 0, 0);
        customerPanel.add(messageLabel, messageGBC);

        accountButton = createButton("Account", UIUtils.BUTTON_BLUE, e -> Main.go("Account"));
        accountButton.setFont(new Font ("Arial", Font.BOLD, 12));
        GridBagConstraints accountGBC = new GridBagConstraints();
            accountGBC.gridx = 0;
            accountGBC.gridy = 1;
            accountGBC.fill = GridBagConstraints.BOTH;
            accountGBC.weightx = 0.5;
            accountGBC.weighty = 0.5;
            accountGBC.insets = new Insets(10, 40, 0, 5);
        customerPanel.add(accountButton, accountGBC);

        logoutButton = createButton("Logout", Color.RED, e -> customer.logout());
        logoutButton.setFont(new Font ("Arial", Font.BOLD, 12));
        GridBagConstraints logoutGBC = new GridBagConstraints();
            logoutGBC.gridx = 1;
            logoutGBC.gridy = 1;
            logoutGBC.fill = GridBagConstraints.BOTH;
            logoutGBC.weightx = 0.5;
            logoutGBC.weightx = 0.5;
            logoutGBC.insets = new Insets(10, 5, 0, 40);
        customerPanel.add(logoutButton, logoutGBC);

        return customerPanel;
    }

    private JButton createButton(String text, Color color, ActionListener action){
        JButton btn = new JButton(text);
        btn.addActionListener(action);
        btn.setFont(UIUtils.FONT_LABEL);
        btn.setForeground(UIUtils.DEFAULT_FOREGROUND);
        btn.setBackground(color);
        btn.setBorder(UIUtils.BUTTON_RAISED);
        btn.setBorderPainted(true);
        btn.setFocusPainted(false);
        return btn;
    }
}