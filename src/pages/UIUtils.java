package pages;
import java.awt.*;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.ParseException;

import main.Main;

public class UIUtils {

    // Fonts
    private static final Font FONT_LABEL = new Font("Arial", Font.BOLD, 16);
    private static final Font FONT_FIELD = new Font ("Arial", Font.PLAIN, 14);
    private static final Font FONT_BUTTON = new Font("Tahoma", Font.BOLD, 14);
    private static final Font FONT_TITLE = new Font("Tahoma", Font.BOLD, 25);

    // Colors
    public static Color BUTTON_BLUE = new Color(0, 123, 255);
    public static Color BUTTON_GREEN = new Color(76, 175, 80);
    public static Color BUTTON_GRAY =new Color(171, 178, 191);
    private static final Color DEFAULT_FOREGROUND = Color.WHITE;

    // Borders
    protected static Border BUTTON_RAISED =BorderFactory.createRaisedBevelBorder();
    

    // Method to create a text entry panel
    public static JPanel createFieldPanel(String name, Boolean required, JTextComponent component) throws IllegalComponentStateException{

        // Create a label for the field
        String asterisk = required ? "<font color='red'>* </font>" : "";
        JLabel label = new JLabel("<html>" + asterisk + name + "</html>");
        label.setFont(FONT_LABEL);
        label.setForeground(DEFAULT_FOREGROUND);

        // Create a panel to encapsulate the field and its label
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(label, BorderLayout.NORTH);
        panel.add(component, BorderLayout.CENTER);
        
        // Set default text field properties
        component.setOpaque(false);
        component.setForeground(DEFAULT_FOREGROUND);
        component.setFont(FONT_FIELD);
        component.setCaretColor(DEFAULT_FOREGROUND);

        // Modify text field depending on its type
        if (component instanceof JPasswordField || component instanceof JTextField) {
            component.setBorder(new MatteBorder(0, 0, 1, 0, Color.WHITE));
        } else if (component instanceof JTextPane){
            component.setBorder(new LineBorder(DEFAULT_FOREGROUND) {
                public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
                    Graphics2D g2d = (Graphics2D)g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setColor(getLineColor());
                    g2d.drawRoundRect(x, y, width-1, height-1, 15, 15);
                }
            });
            component.setOpaque(false);
            component.setBackground(new Color(0, 103, 235));

        } else {
            throw new IllegalArgumentException("UIUtils.beautifyField does not support component type: " + component.getClass().getName());
        }
        return panel;
    }


    // Method to create a title with overloaded methods
    public static JLabel createTitleLabel(String text, int compass, Font font, Color color) {
        JLabel titleLabel = new JLabel(text, compass);
        titleLabel.setFont(font);
        titleLabel.setForeground(color);
        return titleLabel;
    }
    public static JLabel createTitleLabel(String text) {
        return createTitleLabel(text, SwingConstants.CENTER, FONT_TITLE, DEFAULT_FOREGROUND);
    }


    // Method to create a button with overloaded methods
    public static JButton createButton(String text, ActionListener action, Font font, Color foreground, Color background, Border border, Dimension size) {
        JButton button = new JButton(text);
        button.addActionListener(action);
        button.setFont(font);
        button.setForeground(foreground);
        button.setBackground(background);
        button.setBorder(border);
        button.setBorderPainted(true);
        button.setFocusPainted(false);
        button.setPreferredSize(size);
        return button;
    }
    public static JButton createButton(String text, ActionListener action, Color background, Dimension size) {
        return createButton(text, action, FONT_BUTTON, DEFAULT_FOREGROUND, background, BUTTON_RAISED, size);
    }
    public static JButton createButton(String text, ActionListener action) {
        return createButton(text, action, FONT_BUTTON, DEFAULT_FOREGROUND, BUTTON_BLUE, BUTTON_RAISED, new Dimension(0, 35));
    }

    public static JLabel createHyperlink(String start, String link, String end, Runnable action) {
        JLabel htmlLabel = new JLabel("<html><body>" + start + "<a href='' style='color: #ADD8E6;'>" + link + "</a>" + end + "</body></html>");
        htmlLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        htmlLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { action.run(); }
        });
        return htmlLabel;
    }

    public static JFormattedTextField createDateField() {
        try {
            MaskFormatter dateMask = new MaskFormatter("##/##/####");
            dateMask.setPlaceholderCharacter('-');
            return new JFormattedTextField(dateMask);
        } catch (ParseException ex) {
            ex.printStackTrace();
            return null;
        }
    };


    // Method to show an error message popup
    public static void showErr(String message) {
        UIManager.put("OptionPane.messageForeground", DEFAULT_FOREGROUND);
        String htmlMessage = "<html><body><p style='padding: 10px;'>" + message + "</p></body></html>";
        JOptionPane.showMessageDialog(Main.getFrame(), htmlMessage, "Error", JOptionPane.ERROR_MESSAGE);
    }


    // Method to add a component to a container with gbc
    public static void addToGrid(Container container, Component component, GridBagConstraints gbc) {
        container.add(component, gbc);
    }

    // Method to set GridBagConstraints before adding a component to a container
    public static GridBagConstraints createGBC(int gridx, int gridy, double weightx, double weighty, int fill) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = gridx;
        gbc.gridy = gridy;
        gbc.fill = fill;
        gbc.weightx = weightx;
        gbc.weighty = weighty;
        gbc.insets = new Insets(10,10,10,10); // set padding
        return gbc;
    }

}