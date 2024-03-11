package pages;
import javax.swing.*;

import java.awt.*; // Import the FlowLayout class

public class Menu extends Page{

    public Menu() {
        super("Main Menu", new FlowLayout()); // Add this line to invoke the constructor of the superclass
        this.previousPage = this;
    }

    @Override
    protected void populateContent() {
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

        content.add(button1, gbc);
        content.add(button2, gbc);
        content.add(button3, gbc);
        content.add(button4, gbc);
    }
}
