package pages.banner;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionListener;
import database.users.Customer;
import main.Main;
import pages.cart.CartSelect;
import pages.utils.Page;
import static pages.utils.UISettings.*;


public class Banner extends JPanel{
    private JPanel navPanel, titlePanel, variablePanel;
    private JButton backButton, homeButton, loginButton, signupButton, logoutButton, cartButton;
    private final Color BANNER_BACKGROUND = new Color(40, 44, 52);  // background color

    // weight x and y for the two button panels, navPanel and variablePanel
    // affects the stretch applied to the buttons
    private final Double PANEL_WEIGHT_X = 0.25;
    private final Double PANEL_WEIGHT_Y = 1.0;

    // weight x and y for the title panel
    // affects the stretch applied to the title
    private final Double TITLE_WEIGHT_X = 1 - (PANEL_WEIGHT_X * 2);
    private final Double TITLE_WEIGHT_Y = 1.0;


    public Banner(String title) {
        super(new GridBagLayout()); 
        this.setBackground(BANNER_BACKGROUND);


        // Create the navigation panel
        navPanel = new JPanel(new GridBagLayout());
        navPanel.setBackground(BANNER_BACKGROUND);
        GridBagConstraints navGBC = createGBC(
            0, 0, 
            GridBagConstraints.BOTH, 
            PANEL_WEIGHT_X, PANEL_WEIGHT_Y, 
            new Insets(10, 30, 10, 0));
        this.add(navPanel, navGBC);

        backButton = createButton("Back", BUTTON_GRAY, e -> Main.goBack());
        GridBagConstraints backGBC = createGBC(
            0, 0, 
            GridBagConstraints.BOTH, 
            0.33, 1.0, 
            new Insets(10, 0, 10, 5));
        navPanel.add(backButton, backGBC);

        homeButton = createButton("Home", BUTTON_GRAY, e -> Main.go("Menu"));
        GridBagConstraints homeGBC = createGBC(1, 0, 
        GridBagConstraints.BOTH, 
        0.33, 1.0, 
        new Insets(10, 5, 10, 0));
        navPanel.add(homeButton, homeGBC);

        JPanel rightBuffer = new JPanel();
        rightBuffer.setBackground(BANNER_BACKGROUND);
        GridBagConstraints rightGBC = createGBC(
            2, 0, 
            GridBagConstraints.BOTH, 
            0.33, 1.0, 
            new Insets(10, 0, 10, 0));
        navPanel.add(rightBuffer, rightGBC);


        // Create the title panel
        titlePanel = new JPanel(new GridBagLayout());
        titlePanel.setBackground(BANNER_BACKGROUND);
        GridBagConstraints titleGBC = createGBC(
            1, 0, 
            GridBagConstraints.BOTH, 
            TITLE_WEIGHT_X, TITLE_WEIGHT_Y, 
            new Insets(10, 0, 10, 0));
        this.add(titlePanel, titleGBC);

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
            titleLabel.setFont(new Font("Serif", Font.BOLD, 40));
            titleLabel.setForeground(new Color(190, 199, 208));
        titlePanel.add(titleLabel);

        variablePanel = new JPanel(new GridBagLayout());
        variablePanel.setBackground(BANNER_BACKGROUND);
        variablePanel.setPreferredSize(navPanel.getPreferredSize());
        GridBagConstraints variableGBC = createGBC(
            2, 0, 
            GridBagConstraints.BOTH, 
            PANEL_WEIGHT_X, PANEL_WEIGHT_Y, 
            new Insets(10, 0, 10, 30));
        this.add(variablePanel, variableGBC);

        cartButton = createButton("Cart", BUTTON_GRAY, e -> Main.goNew(new CartSelect(), "Cart"));
        GridBagConstraints cartGBC = createGBC(
            0, 0, 
            GridBagConstraints.BOTH, 
            1, 1.0, 
            new Insets(10, 0, 10, 5));
            cartGBC.gridheight = 2;
        variablePanel.add(cartButton, cartGBC);

        if (!(Main.user instanceof Customer)){
            
            loginButton = createButton("Log In", BUTTON_BLUE, e -> Main.go("Login"));
            loginButton.setPreferredSize(cartButton.getPreferredSize());
            GridBagConstraints loginGBC = createGBC(
                1, 0, 
                GridBagConstraints.BOTH, 
                1, 1.0, 
                new Insets(10, 5, 10, 5));
                loginGBC.gridheight = 2;
            variablePanel.add(loginButton, loginGBC);
            
            signupButton = createButton("Sign Up", BUTTON_GREEN, e -> Main.go("Signup"));
            signupButton.setPreferredSize(cartButton.getPreferredSize());
            GridBagConstraints signupGBC = createGBC(
                2, 0, 
                GridBagConstraints.BOTH, 
                1, 1.0, 
                new Insets(10, 5, 10, 0));
                signupGBC.gridheight = 2;
            variablePanel.add(signupButton, signupGBC);
            
        } else {
            Customer customer = (Customer) Main.user;
            String firstName = customer.getName().split(" ")[0]; // Get the first name
            JLabel messageLabel = new JLabel("Welcome, "+ firstName + "!", SwingConstants.CENTER);
            messageLabel.setFont(FONT_LABEL);
            GridBagConstraints messageGBC = createGBC(
                1, 0, 
                GridBagConstraints.BOTH, 
                1, 0.5, 
                new Insets(0, 0, 0, 0));
            messageGBC.anchor = GridBagConstraints.PAGE_END;
            variablePanel.add(messageLabel, messageGBC);
    
            logoutButton = createButton("Logout", BUTTON_RED, e -> {customer.logout(); Main.go("Menu"); });
            logoutButton.setFont(new Font ("Tahoma", Font.BOLD, 12));
            GridBagConstraints logoutGBC = createGBC(
                1, 1, 
                GridBagConstraints.BOTH, 
                0, 0, 
                new Insets(5,50,0,50));
            variablePanel.add(logoutButton, logoutGBC);
        }

        this.revalidate();
        this.repaint();
    }



    private JButton createButton(String text, Color color, ActionListener action){
        JButton btn = new JButton(text);
        btn.addActionListener(action);
        btn.setFont(FONT_LABEL);
        btn.setForeground(DEFAULT_FOREGROUND);
        btn.setBackground(color);
        btn.setBorder(BUTTON_RAISED);
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