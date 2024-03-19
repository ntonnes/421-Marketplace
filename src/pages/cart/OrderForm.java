package pages.cart;

import main.Main;
import pages.utils.ColumnPage;
import pages.utils.Popup;

import java.sql.*;
import java.text.*;
import java.util.Date;
import javax.swing.*;

import database.Database;
import database.users.Customer;
import static pages.utils.UISettings.*;


public class OrderForm extends ColumnPage {

    private static JTextField emailField = new JTextField(20);
    private static JTextField cardNumberField = new JTextField(20);
    private static JTextField addressField = new JTextField(20);
    private JButton orderButton;

    public OrderForm() {
        super("Fill in Order Details");

        emailField.addActionListener(e -> cardNumberField.requestFocus());
        cardNumberField.addActionListener(e -> addressField.requestFocus());
        addressField.addActionListener(e -> orderButton.doClick());
    }

    @Override
    protected void populateContent() {

        addBuffer(0.05);

        // Add the components to the panel
        if (!(Main.user instanceof Customer)) {
            JPanel emailEntry = createFieldPanel("Email", true, emailField);
            addComponent(emailEntry, 0);
            addBuffer(0.02);
        }
        JPanel cardNumberEntry = createFieldPanel("Last Name:", true, cardNumberField);
        JPanel addressEntry = createFieldPanel("Email:", true, addressField);
        orderButton = createButton("Place Order", BUTTON_BLUE, e -> submit());

        addComponent(cardNumberEntry, 0);
        addBuffer(0.02);
        addComponent(addressEntry, 0);
        addBuffer();
    }


    private void submit() {
        String email;
        if (Main.user instanceof Customer) {
            email = ((Customer) Main.user).getEmail();
        } else {
            email = emailField.getText();
        }
        String cardNumber = cardNumberField.getText();
        String address = addressField.getText();

        // add order logic here
    }
}
