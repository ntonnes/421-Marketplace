package pages;
import main.Main;

import javax.swing.*;

import database.users.Customer;

import java.awt.*; // Import the FlowLayout class
import java.awt.event.WindowEvent;

public class Menu extends Page{

    public Menu() {
        super("Main Menu", new FlowLayout()); // Add this line to invoke the constructor of the superclass
        this.previousPage = this;
    }

    private void quit(){
        System.out.println("Exiting the program...");
        JFrame frame = Main.getFrame();
        frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
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

        JButton button3 = new JButton("Leave a Review");
        button3.addActionListener(e -> {
            if (!(Main.user instanceof Customer)) {
                Utils.showErr("Please log into a customer account to leave a review");
            } else { goPage(new Review()); }
        });

        // Exits the program and prints a console message
        JButton button4 = new JButton("Quit");
        button4.addActionListener(e -> quit());

        content.add(button1, gbc);
        content.add(button2, gbc);
        content.add(button3, gbc);
        content.add(button4, gbc);
    }
}
