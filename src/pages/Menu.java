package pages;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;

import main.Main;
import database.users.Customer;

public class Menu extends ColumnPage{
    private JButton b1, b2, b3, b4;

    public Menu() {
        super("Main Menu"); // Add this line to invoke the constructor of the superclass
    }

    private void quit(){
        System.out.println("Exiting the program...");
        JFrame frame = Main.getFrame();
        frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
    }

    @Override
    protected void populateContent() {

        // Initialize buttons to execute tasks
        // TODO: Implement Task 1
        b1 = createButton("Task 1", BUTTON_GRAY, e -> System.out.println("You selected task 1"));
        addComponent(b1, 0.05);

        // TODO: Implement Task 2
        b2 = createButton("Task 2", BUTTON_GRAY, e -> System.out.println("You selected task 2"));
        addComponent(b2, 0.05);

        b3 = createButton("Leave a Review", BUTTON_GRAY, e -> {
            if (!(Main.user instanceof Customer)) {
                UIUtils.showErr("Please log into a customer account to leave a review");
            } else { Main.goNew(new ReviewSelect(), "Review Select"); }
        });
        addComponent(b3, 0.05);

        // Exits the program and prints a console message
        b4 = createButton("Quit", BUTTON_GRAY, e -> quit());
        addComponent(b4, 0.05);

        GridBagConstraints verticalGBC = createGBC(
            1, 4, 
            GridBagConstraints.BOTH, 
            .3, .8, 
            new Insets(0, 0, 0, 0));
        content.add(new JPanel(), verticalGBC);

        GridBagConstraints horizontalGBC = createGBC(
            0, 5, 
            GridBagConstraints.VERTICAL, 
            .35, 1, 
            new Insets(0, 0, 0, 0));
        content.add(new JPanel(), horizontalGBC);
        
        horizontalGBC.gridx = 2;
        content.add(new JPanel(), horizontalGBC);
    }

}
