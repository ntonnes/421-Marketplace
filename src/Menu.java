import javax.swing.*;
import java.awt.*;

public class Menu {
    private static JFrame frame;
    private static JPanel buttonPanel;
    private static JLabel nameLabel;
    private static Login.User user = new Login.User("guest",null,null,null,null);

    // This class contains the name, email, and userID of the user who is logged into the app. user=NULL until
    // the user completes the login task, after which the user instance becomes populated with that user's info.
    // Use this information to execute tasks and queries specific to the user.
    // TODO: force the user to either log in, or continue as guest. If the user is a guest, generate a new userID
    // TODO: and add it to the database. Delete the user from the database when the application is closed.
    
    public static JFrame getFrame() { return frame; }
    public static JPanel getButtons() { return buttonPanel; }
    public static JLabel getNameLabel() { return nameLabel; }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(Menu::createAndShowGUI);
    }

    // This is all for the GUI
    private static void createAndShowGUI() {
        UIManager.put("Panel.background", Color.DARK_GRAY);
        UIManager.put("Button.background", Color.LIGHT_GRAY);
        UIManager.put("Button.foreground", Color.BLACK);
        UIManager.put("Label.foreground", Color.WHITE);

        // Initializes the application managed by the OS
        frame = new JFrame("421 Marketplace");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000,800);

        // Places the large title in the top + center of the window
        JLabel title = new JLabel("421 Marketplace", SwingConstants.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, 30));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        // Places a welcome message for the user beneath the title
        nameLabel = new JLabel();
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setHorizontalAlignment(JLabel.CENTER);
        nameLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        // Groups the title and welcome message in a panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(title, BorderLayout.NORTH);
        topPanel.add(nameLabel, BorderLayout.CENTER);
        topPanel.setBackground(Color.DARK_GRAY);

        // Sets the vertical positions of the banner panel (title+welcome message) and the button panel.
        buttonPanel = new JPanel(new GridBagLayout());
        frame.getContentPane().add(topPanel, BorderLayout.NORTH);
        frame.getContentPane().add(buttonPanel, BorderLayout.CENTER);

        // Sets the horizontal positions of the banner panel (title+welcome message) and the button panel.
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);

        // Initialize buttons to execute tasks
        // TODO: Implement Task 1
        JButton button1 = new JButton("Task 1");
        button1.addActionListener(e -> System.out.println("You selected task 1"));

        // TODO: Implement Task 2
        JButton button2 = new JButton("Task 2");
        button2.addActionListener(e -> System.out.println("You selected task 2"));

        // TODO: Implement Task 3
        JButton button3 = new JButton("Task 3");
        button3.addActionListener(e -> System.out.println("You selected task 3"));

        // Exits the program and prints a console message
        // TODO: Handle the logged-in user state
        JButton button4 = new JButton("Quit");
        button4.addActionListener(e -> {
            System.out.println("Exiting the program...");
            System.exit(0);
        });

        // Calls the method to complete the login task when selected
        JButton loginButton = new JButton("Login/Sign Up");
        loginButton.addActionListener(e -> Login.showLoginPanel());

        // Adds the buttons to the GUI
        buttonPanel.add(button1, gbc);
        buttonPanel.add(button2, gbc);
        buttonPanel.add(button3, gbc);
        buttonPanel.add(button4, gbc);
        buttonPanel.add(loginButton, gbc);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void setUser(Login.User usr) {
        Menu.user = usr;
        nameLabel.setText("Welcome, " + user.name + "!");
    }
}