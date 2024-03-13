package pages;

import javax.swing.*;

import database.users.*;

import java.awt.*;

import main.Main;
import pages.forms.Login;
import pages.forms.Signup;

public class Banner extends JPanel{
    private JPanel navContent;
    private JLabel titleLabel;
    private JPanel accountContent;
    private Color backgroundColor = new Color(40, 44, 52);

    public Banner(String title) {
        this.setLayout(new BorderLayout(10, 10)); 
        this.setBackground(backgroundColor);
        this.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Create the back button with custom colors and padding
        navContent = new JPanel(new FlowLayout(FlowLayout.LEFT));
        navContent.setPreferredSize(new Dimension(Main.getFrame().getWidth() / 4, 0));
        navContent.setOpaque(false);
        navContent.add(Utils.styleButton("Back", new Color(171, 178, 191),100,40, e -> Main.goBack()));
        navContent.add(Utils.styleButton("Home", new Color(171, 178, 191),100,40, e -> Main.goPage(Main.mainMenu)));
        this.add(navContent, BorderLayout.WEST);

        // Create the title label with large font and white color
        titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 40));
        titleLabel.setForeground(new Color(190, 199, 208)); // Light gray
        this.add(titleLabel, BorderLayout.CENTER); // Add to the center

        updateBanner();
    }

    public void updateBanner() {
        // Remove the old accountContent
        if (accountContent != null) {
            this.remove(accountContent);
        }

        // Add the new accountContent
        this.add(createAccountContent(), BorderLayout.EAST);
        this.revalidate();
        this.repaint();
    }

    private JPanel createAccountContent() {
        accountContent = new JPanel();
        accountContent.setBackground(backgroundColor);
        accountContent.setPreferredSize(new Dimension(Main.getFrame().getWidth() / 4, 0));
        
        if (Main.user instanceof Customer) {
            Customer customer = (Customer) Main.user;
            accountContent.setLayout(new GridBagLayout());
            accountContent.setOpaque(false); 
    
            JLabel welcomeLabel = new JLabel("Welcome,");
            JLabel nameLabel = new JLabel(customer.getName()+ "!");
            welcomeLabel.setFont(new Font("Serif", Font.BOLD, 15));
            nameLabel.setFont(new Font("Serif", Font.ITALIC, 15));
            accountContent.add(welcomeLabel, Utils.makeGbc(0,0,0,0,0));
            accountContent.add(nameLabel, Utils.makeGbc(1,0,0,0,0));

    
            JButton logoutButton = Utils.styleButton("Logout", Color.RED, 80, 50, e -> customer.logout());
            GridBagConstraints logoutGBC = Utils.makeGbc(0,0,0,0,0);
            logoutGBC.gridx = 1;
            logoutGBC.gridheight = 1;
            accountContent.add(logoutButton, logoutGBC); // Place the logout button to the right of the welcome label
        } else {
            accountContent.setLayout(new FlowLayout(FlowLayout.RIGHT));
    
            accountContent.add(Utils.styleButton("Log in", new Color(0, 123, 255),100,40, e -> {
                if (Main.getPage() instanceof Login){
                    return;
                } else if (Main.getLastPage() instanceof Login){
                    Main.goBack();
                } else {
                    Main.goPage(new Login());
                }
            }));
    
            accountContent.add(Utils.styleButton("Sign up", new Color(76, 175, 80),100,40, e -> {
                if (Main.getPage() instanceof Signup){
                    return;
                } else if (Main.getLastPage() instanceof Signup){
                    Main.goBack();
                } else {
                    Main.goPage(new Signup());
                }
            }));
        }
        return accountContent;
    }
}