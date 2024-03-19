package pages.cart;

import main.Main;
import pages.utils.ColumnPage;
import pages.utils.Popup;

import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
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
    private static JTextField expDateField = new JTextField(20);
    private static JTextField addressField = new JTextField(20);
    private JButton orderButton;

    public OrderForm() {
        super("Fill in Order Details");

        emailField.addActionListener(e -> cardNumberField.requestFocus());
        cardNumberField.addActionListener(e -> expDateField.requestFocus());
        expDateField.addActionListener(e -> addressField.requestFocus());
        addressField.addActionListener(e -> orderButton.doClick());
    }

    @Override
    protected void populateContent() {

        addBuffer(0.05);

        // Add the components to the panel
        JPanel emailEntry = createFieldPanel("Email", true, emailField);
        JPanel cardNumberEntry = createFieldPanel("Card Number:", true, cardNumberField);
        JPanel expDateEntry = createFieldPanel("Expiration Date (mm/dd/yyyy):", true, expDateField);
        JPanel cardPanel = doublePanel(cardNumberEntry, expDateEntry);
        JPanel addressEntry = createFieldPanel("Shipping Address:", true, addressField);
        orderButton = createButton("Place Order", BUTTON_BLUE, e -> submit());

        addComponent(emailEntry, 0);
        addBuffer(0.02);
        if (Main.user instanceof Customer) {
            Customer customer = (Customer) Main.user;
            emailField.setText(customer.getEmail());
            addComponent(createCardBox(), 0);
            addBuffer(0.02);
        }
        addComponent(cardPanel, 0);
        addBuffer(0.02);
        addComponent(addressEntry, 0);
        addBuffer(0.02);
        addComponent(orderButton, 0.05);
        addBuffer(0.96);
        addBuffer();
    }

    public JPanel createCardBox() {
        JComboBox<Card> cardBox = getSavedCards();
        cardBox.addActionListener(e -> {
            if (cardBox.getSelectedIndex() != 0) {
                Card card = (Card) cardBox.getSelectedItem();
                cardNumberField.setText(card.cardNumber);
                expDateField.setText(card.expDate);
            }
        });
        JLabel cardLabel = new JLabel("Use a Saved Card:");
            cardLabel.setFont(new Font ("Arial", Font.BOLD, 16));
        JPanel cardPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.anchor = GridBagConstraints.WEST;
            gbc.fill = GridBagConstraints.HORIZONTAL;
        cardPanel.add(cardLabel, gbc);
            gbc.gridx = 1;
            gbc.weightx = 0.5;
            gbc.insets = new Insets(0, 10, 0, 0);
        cardPanel.add(cardBox, gbc);
        return cardPanel;
    }

    private class Card {
        private String cardNumber;
        private String expDate;
        private String display;

        public Card(String cardNumber, String expDate) {
            this.cardNumber = cardNumber;
            this.expDate = expDate;
            if (cardNumber == null) {
                this.display = "Select a saved card:";
            } else {
                this.display = "**** **** **** " + cardNumber.substring(cardNumber.length() - 4);
            }
        }
    }

    private JComboBox<Card> getSavedCards() {
        JComboBox<Card> savedCards = new JComboBox<>();
        Card placeholder = new Card(null, null);
        placeholder.display = "Select a saved card";
        savedCards.addItem(placeholder);
        savedCards.setSelectedIndex(0);

        savedCards.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Card) {
                    setText(((Card) value).display);
                }
                return this;
            }
        });

        int userID = Main.user.getUserID();
        try (Connection conn = DriverManager.getConnection(Database.DB_URL, Database.USER, Database.PASS)) {
            try (PreparedStatement stmt = conn.prepareStatement("SELECT cardNum FROM Card WHERE userID = ?")) {
                stmt.setInt(1, userID);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    String cardNumber = rs.getString("cardNum");
                    String expDate = rs.getString("CardExp");
                    savedCards.addItem(new Card (cardNumber, expDate));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error while retrieving saved cards from the database");
            e.printStackTrace();
        }
        return savedCards;
    }

    private void submit() {
        String email;
        String cardNumber = cardNumberField.getText();
        String address = addressField.getText();
    }
}
