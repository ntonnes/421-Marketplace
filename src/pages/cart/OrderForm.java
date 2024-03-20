package pages.cart;

import main.Main;
import pages.utils.ColumnPage;
import pages.utils.Popup;

import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Random;

import javax.swing.*;

import database.Customer;
import database.Database;

import static pages.utils.UISettings.*;


public class OrderForm extends ColumnPage {

    private static JTextField emailField = new JTextField(20);
    private static JTextField cardNumberField = new JTextField(20);
    private static JFormattedTextField expDateField = createDateField();
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
        JPanel expDateEntry = createFieldPanel("Card Expiration (yyyy/mm/dd):", true, expDateField);
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
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
                String formattedDate = card.expDate.format(formatter);
                expDateField.setText(formattedDate);
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
        private LocalDate expDate;
        private String display;

        public Card(String cardNumber, LocalDate expDate) {
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
        try (Connection conn = Database.connect()) {
            try (PreparedStatement stmt = conn.prepareStatement("SELECT cardNum, CardExp FROM Card WHERE userID = ?")) {
                stmt.setInt(1, userID);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    String cardNumber = rs.getString("cardNum");
                    java.sql.Date sqlDate = rs.getDate("CardExp");
                    LocalDate expDate = sqlDate.toLocalDate();
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
        String email = emailField.getText();
        long cardNumber = Long.parseLong(cardNumberField.getText());
        String expDateString = expDateField.getText();
        java.sql.Date sqlDate = null;
        String address = addressField.getText();

        // Validate card expiration date
        if (!expDateString.isEmpty() && !expDateString.equals("----/--/--")) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
                LocalDate expDate = LocalDate.parse(expDateString, formatter);

                // Check if the date is in the future
                if (expDate.isBefore(LocalDate.now())) {
                    Popup.showErr("The card you are trying to use is expired.");
                    expDateField.setText(""); // Reset the expDateField
                    return;
                }

                // Convert LocalDate to java.sql.Date
                sqlDate = java.sql.Date.valueOf(expDate);
            } catch (DateTimeParseException e) {
                Popup.showErr("Invalid Date format. Please use yyyy/mm/dd");
                expDateField.setText(""); // Reset the expDateField
                return;
            }
        } else if (!expDateString.equals("----/--/--")) {
            Popup.showErr("Invalid Card Expiration Date. Try again.");
            expDateField.setText(""); // Reset the expDateField
            return;
        }

        createOrder(cardNumber, sqlDate, address, email);
    }

    public void createOrder(long cardNum, java.sql.Date cardExp, String address, String email) {
        System.out.println("Creating order...");
        Integer userID = Main.user.getUserID();
        BigDecimal total = BigDecimal.valueOf(CartSelect.getTotal());
        System.out.println("Total: " + total);
        int orderID=0;

        try (Connection conn = Database.connect()) {

            // Check if there are enough serial numbers available
            if (!(areSerialNosAvailable(conn))) {
                return;
            }

            // Check if the card exists in the database, validate, and insert if needed
            try (PreparedStatement stmt = conn.prepareStatement("SELECT CardExp FROM Card WHERE CardNum = ?")) {
                stmt.setLong(1, cardNum);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    java.sql.Date existingCardExp = rs.getDate("CardExp");
                    if (!existingCardExp.equals(cardExp)) {
                        Popup.showErr("The expiration date does not match the card number. Please try again.");
                    } else {
                        System.out.println("Card validated: " + cardNum + " " + cardExp);
                    }
                } else {
                    // Card does not exist, insert it
                    Boolean saved = false;
                    try (PreparedStatement insertStmt = conn.prepareStatement("INSERT INTO Card (CardNum, CardExp, UserID) VALUES (?, ?, ?)")) {
                        insertStmt.setLong(1, cardNum);
                        insertStmt.setDate(2, cardExp);

                        // If the user is a customer, ask if they want to save the card
                        if (Main.user instanceof Customer) {
                            int choice = Popup.showConfirm("Would you like to save the card?");
                            // If the user answers yes, save the card with their userID
                            if (choice == JOptionPane.YES_OPTION) {
                                insertStmt.setInt(3, Main.user.getUserID());
                                saved = true;
                            // If the user answers no, save the card with a null userID
                            } else {
                                insertStmt.setNull(3, Types.INTEGER);
                            }
                        // If the user is a guest, save the card with a null userID
                        } else {
                            insertStmt.setNull(3, Types.INTEGER);
                        }
                        insertStmt.executeUpdate();
                        System.out.println("Card successfully inserted: " + cardNum + " " + cardExp + ". Saved to a user: " + saved);
                    }
                }
            }
            
            // Get a unique random OrderID
            Random rand = new Random();
            boolean unique = false;
            while (!unique) {
                orderID = rand.nextInt(900000000) + 100000000;
                try (PreparedStatement stmt = conn.prepareStatement("SELECT OrderID FROM Order WHERE OrderID = ?")) {
                    stmt.setInt(1, orderID);
                    ResultSet rs = stmt.executeQuery();
                    if (!rs.next()) {
                        unique = true;  // The OrderID is unique
                        System.out.println("Unique OrderID found: " + orderID);
                    }
                }
            }

            // Insert into Order table
            java.sql.Date sqlDate = java.sql.Date.valueOf(LocalDate.now());
            try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO Order(OrderID, DeliverAdd, Total, Date, Email, CardNum) VALUES (?, ?, ?, ?, ?, ?)")) {
                stmt.setInt(1, orderID);
                stmt.setString(2, address);
                stmt.setBigDecimal(3, total);
                stmt.setDate(4, sqlDate);
                stmt.setString(5, email);
                stmt.setLong(6, cardNum);
                stmt.executeUpdate();
            }

            // Insert into Purchased table
            try (PreparedStatement stmt = conn.prepareStatement("SELECT ModelID, Copies FROM InCart WHERE UserID = ?")) {
                stmt.setInt(1, userID);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    int modelID = rs.getInt("ModelID");
                    int copies = rs.getInt("Copies");
                    for (int i = 0; i < copies; i++) {
                        Integer serialNo = getAvailableSerialNo(conn, modelID);
                        if (serialNo == null) {
                            System.out.println("No available serial numbers for modelID: " + modelID);
                            return;
                        }
                        try (PreparedStatement stmt2 = conn.prepareStatement("INSERT INTO Purchased(ModelID, SerialNo, OrderID, ShipmentNo, ShipperName) VALUES (?, ?, ?, ?, ?)")) {
                            stmt2.setInt(1, modelID);
                            stmt2.setInt(2, serialNo);
                            stmt2.setInt(3, orderID);
                            Shipment shipment = getShipment(conn);
                            stmt2.setInt(4, shipment.getShipmentNo());
                            stmt2.setString(5, shipment.getShipperName());
                            stmt2.executeUpdate();
                            System.out.println("Purchased: model " + modelID + ", serialNo " + serialNo + ", orderID " + orderID + 
                            " shipmentNo:" + shipment.getShipmentNo() + " shipper name:" + shipment.getShipperName() + "\n");
                            Popup.showMsg("Order placed successfully! Your OrderID is: " + orderID + ". Check your email for the shipment details.");
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error while creating order");
            e.printStackTrace();
        }
        Main.go("Menu");
    }

    private Shipment getShipment(Connection conn) {
        Shipment shipment = null;
        try {
            // Get the count of all shipments
            try (PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) AS count FROM Shipment")) {
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    int count = rs.getInt("count");

                    // Generate a random index
                    Random rand = new Random();
                    int index = rand.nextInt(count);

                    // Select the shipment at the random index
                    try (PreparedStatement stmt2 = conn.prepareStatement("SELECT * FROM Shipment LIMIT 1 OFFSET ?")) {
                        stmt2.setInt(1, index);
                        ResultSet rs2 = stmt2.executeQuery();
                        if (rs2.next()) {
                            int shipmentNo = rs2.getInt("ShipmentNo");
                            String shipperName = rs2.getString("ShipperName");
                            shipment = new Shipment(shipmentNo, shipperName);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error while getting shipment");
            e.printStackTrace();
        }
        return shipment;
    }


    private class Shipment {
        private int shipmentNo;
        private String shipperName;

        public Shipment(int shipmentNo, String shipperName) {
            this.shipmentNo = shipmentNo;
            this.shipperName = shipperName;
        }
        public int getShipmentNo() {
            return shipmentNo;
        }
        public String getShipperName() {
            return shipperName;
        }
    }

    private Integer getAvailableSerialNo(Connection conn, int modelID) {
        int serialNo = -1;
        try (PreparedStatement stmt = conn.prepareStatement(
            "SELECT SerialNo FROM Product " +
            "WHERE ModelID = ? AND SerialNo NOT IN "+
            "(SELECT SerialNo FROM Purchased WHERE ModelID = ?)")) {
            stmt.setInt(1, modelID);
            stmt.setInt(2, modelID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                serialNo = rs.getInt("SerialNo");
                System.out.println("Using serial number: " + serialNo + " for modelID: " + modelID);
            } else {
                System.out.println("No available serial numbers for modelID: " + modelID);
                return null;
            }
            return serialNo;
        } catch (SQLException e) {
            System.out.println("Error while getting available serial number");
            e.printStackTrace();
            return null;
        }
    }

    // Checks if there are enough serial numbers available for the items in the cart
    private Boolean areSerialNosAvailable(Connection conn) {
        int userID = Main.user.getUserID();

        // Get the number of items in the cart for each modelID
        try (PreparedStatement stmt = conn.prepareStatement("SELECT ModelID, Copies FROM InCart WHERE UserID = ?")) {
            stmt.setInt(1, userID);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int modelID = rs.getInt("ModelID");
                int copies = rs.getInt("Copies");

                // Get the count of available serial numbers for each modelID
                try (PreparedStatement stmt2 = conn.prepareStatement(
                    "SELECT COUNT(*) AS count FROM Product " +
                    "WHERE ModelID = ? AND (SerialNo, ModelID) NOT IN " +
                    "(SELECT SerialNo, ModelID FROM Purchased WHERE ModelID = ?)")) {
                    stmt2.setInt(1, modelID);
                    stmt2.setInt(2, modelID);
                    ResultSet rs2 = stmt2.executeQuery();
                    if (rs2.next()) {
                        int count = rs2.getInt("count");
                        if (count < copies) {
                            Popup.showErr("Not enough serial numbers available for modelID: " + modelID + ". In Cart: " + copies + ", Available: " + count);
                            return false;
                        } else {
                            System.out.println("Available serial numbers for modelID: " + modelID + " is " + count + ". In Cart: " + copies);
                        }
                    } else {
                        Popup.showErr("No available serial numbers for modelID: " + modelID);
                        return false;
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error while checking availability of serial numbers");
            e.printStackTrace();
            return false;
        }
        return true;
    }
}