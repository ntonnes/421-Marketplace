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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import javax.swing.*;

import database.Customer;
import database.Database;

import static pages.utils.UISettings.*;


public class OrderForm extends ColumnPage {

    private static JTextField emailField = new JTextField(20);
    private static JTextField cardNumberField = new JTextField(20);
    private static JTextField expDateField = new JTextField(20);
    private static JTextField addressField = new JTextField(20);

    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
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
        try (Connection conn = Database.connect()) {
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
        String email = emailField.getText();
        String cardNumber = cardNumberField.getText();
        String expDate = expDateField.getText();
        String address = addressField.getText();

        createOrder(cardNumber, expDate, address, email);

        try (Connection conn = DriverManager.getConnection(Database.DB_URL, Database.USER, Database.PASS);
             PreparedStatement checkCard = conn.prepareStatement("INSERT INTO Card VALUES (?,?,?)");
             PreparedStatement addCard = conn.prepareStatement("INSERT INTO Card VALUES (?,?,?)");
             PreparedStatement addOrder = conn.prepareStatement("SELECT * FROM Customer WHERE email = ?")) {


            /*
            // Check if email already exists
            if (resultSet.next()) {
                Popup.showErr("An account with that email already exists.");
                return;

                // If email is unique, create the customer
            } else {
                insertStmt.setInt(1, userID);
                insertStmt.setString(2, dob);
                insertStmt.setString(3, password);
                insertStmt.setString(4, email);
                insertStmt.setString(5, name);
                insertStmt.executeUpdate();

                Main.setUser(new Customer(userID, name, email, password, dob));
                System.out.println("\nSuccessfully registered user " + name + ":\n" + Main.user.toString() + "\n");
                Main.goBack();
                return;
            }
            */
        } catch (SQLException e) {
            e.printStackTrace();
            Popup.showErr("An error occurred while executing an SQL statement.");
            return;
        }

    }

    public void createOrder(String cardNum, String cardExp, String address, String email) {
        System.out.println("Creating order...");
        Integer userID = Main.user.getUserID();
        BigDecimal total = BigDecimal.valueOf(CartSelect.getTotal());
        System.out.println("Total: " + total);
        Date date = new Date();
        System.out.println("Date: " + date);

        try (Connection conn = Database.connect()) {

            // Check if there are enough serial numbers available
            if (!(areSerialNosAvailable(conn))) {
                return;
            }
            
            // Get a unique random OrderID
            Random rand = new Random();
            int orderID=0;
            boolean unique = false;
            while (!unique) {
                orderID = rand.nextInt(99999, 1000000);  // Generate a random number between 0 and 999999
                try (PreparedStatement stmt = conn.prepareStatement("SELECT OrderID FROM Order WHERE OrderID = ?")) {
                    stmt.setInt(1, orderID);
                    ResultSet rs = stmt.executeQuery();
                    if (!rs.next()) {
                        unique = true;  // The OrderID is unique
                    }
                }
            }
            System.out.println("OrderID: " + orderID);

            // Insert into Card table if necessary
            try (PreparedStatement stmt1 = conn.prepareStatement("SELECT COUNT(*) as count FROM Card where CardNum = ?");
                 PreparedStatement stmt2 = conn.prepareStatement("INSERT INTO Card(CardNum, CardExp, UserID) VALUES (?, ?, ?)")) {
                stmt1.setString(1, cardNum);
                ResultSet res = stmt1.executeQuery();
                if (res.next()) {
                    int count = res.getInt("count");
                    if (count == 0) {
                        stmt2.setString(1, cardNum);
                        stmt2.setString(2, cardExp);
                        if (Main.user instanceof Customer) {
                            stmt2.setInt(3, userID);
                        } else {
                            stmt2.setNull(3, Types.INTEGER);
                        }
                        stmt2.executeUpdate();
                    }
                }
            }

            // Insert into Order table
            try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO Order(OrderID, DeliverAdd, Total, Date, Email, CardNum) VALUES (?, ?, ?, ?, ?, ?)")) {
                stmt.setInt(1, 375847364);
                stmt.setString(2, address);
                stmt.setBigDecimal(3, total);
                Date d = new java.sql.Date(date.getTime());
                String sqlDate = sdf.format(d);
                stmt.setString(4, sqlDate);
                stmt.setString(5, email);
                stmt.setString(6, cardNum);
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
                        try (PreparedStatement stmt2 = conn.prepareStatement("INSERT INTO Purchased(ModelID, SerialNo, OrderID, ShipmentNo, ShipperName) VALUES (?, ?, ?, ?, ?)")) {
                            stmt2.setInt(1, modelID);
                            stmt2.setInt(2, getAvailableSerialNo(conn, modelID));
                            stmt2.setInt(3, orderID);
                            Shipment shipment = getShipment(conn);
                            stmt2.setInt(4, shipment.getShipmentNo());
                            stmt2.setString(5, shipment.getShipperName());
                            stmt2.executeUpdate();
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error while creating order");
            e.printStackTrace();
        }
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
                            // Assuming Shipment has fields ShipperNo and ShipperName
                            int shipperNo = rs2.getInt("ShipperNo");
                            String shipperName = rs2.getString("ShipperName");
                            shipment = new Shipment(shipperNo, shipperName);
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
        try (PreparedStatement stmt = conn.prepareStatement("SELECT SerialNo FROM Product where ModelID = ? AND SerialNo NOT IN (SELECT SerialNo FROM Purchased)")) {
            stmt.setInt(1, modelID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                serialNo = rs.getInt("SerialNo");
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

    private Boolean areSerialNosAvailable(Connection conn) {
        int userID = Main.user.getUserID();

        try (PreparedStatement stmt = conn.prepareStatement("SELECT ModelID, Copies FROM InCart WHERE UserID = ?")) {
            stmt.setInt(1, userID);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int modelID = rs.getInt("ModelID");
                int copies = rs.getInt("Copies");

                try (PreparedStatement stmt2 = conn.prepareStatement("SELECT COUNT(*) AS count FROM Product WHERE ModelID = ? AND SerialNo NOT IN (SELECT SerialNo FROM Purchased WHERE ModelID = ?)")) {
                    stmt2.setInt(1, modelID);
                    stmt2.setInt(2, modelID);
                    ResultSet rs2 = stmt2.executeQuery();
                    if (rs2.next()) {
                        int count = rs2.getInt("count");
                        if (count < copies) {
                            Popup.showErr("Not enough serial numbers available for modelID: " + modelID + ". In Cart: " + copies + ", Available: " + count);
                            return false;
                        }
                    }
                    System.out.println("No available serial numbers for modelID: " + modelID);
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