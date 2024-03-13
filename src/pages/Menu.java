package pages;
import main.Main;

import javax.swing.*;

import database.users.Customer;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.WindowEvent;

public class Menu extends Page{
    private JPanel buttonPanel;

    public Menu() {
        super(null, "Main Menu"); // Add this line to invoke the constructor of the superclass
        this.lastPage = this;
    }

    private void quit(){
        System.out.println("Exiting the program...");
        JFrame frame = Main.getFrame();
        frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
    }

    @Override
    protected void populateContent() {

        buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setPreferredSize(new Dimension(content.getWidth() / 3, 0));

        // Initialize buttons to execute tasks
        // TODO: Implement Task 1
        JButton button1 = UIUtils.createButton("Task 1", e -> System.out.println("You selected task 1"), UIUtils.BUTTON_GRAY, new Dimension(100, 40));

        // TODO: Implement Task 2
        JButton button2 = UIUtils.createButton("Task 2", e -> System.out.println("You selected task 2"), UIUtils.BUTTON_GRAY, new Dimension(100, 40));

        JButton button3 = UIUtils.createButton("Leave a Review", e -> {
            if (!(Main.user instanceof Customer)) {
                UIUtils.showErr("Please log into a customer account to leave a review");
            } else { Main.goPage(new Review()); }
        }, UIUtils.BUTTON_GRAY, new Dimension(100, 40));

        // Exits the program and prints a console message
        JButton button4 = UIUtils.createButton("Task 2", e -> quit(), UIUtils.BUTTON_GRAY, new Dimension(100, 40));

        UIUtils.addToGrid(content, button1, UIUtils.createGBC(0, 0, 1,1, GridBagConstraints.HORIZONTAL));
        UIUtils.addToGrid(content, button2, UIUtils.createGBC(0, 1, 1,1, GridBagConstraints.HORIZONTAL));
        UIUtils.addToGrid(content, button3, UIUtils.createGBC(0, 2, 1,1, GridBagConstraints.HORIZONTAL));
        UIUtils.addToGrid(content, button4, UIUtils.createGBC(0, 3, 1,1, GridBagConstraints.HORIZONTAL));
    }

}
