package pages.utils;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.*;


import java.awt.*;
import java.awt.event.*;
import java.text.ParseException;

public abstract class Page extends JPanel {

    // Fonts
    public static final Font FONT_LABEL = new Font("Arial", Font.BOLD, 16);
    protected static final Font FONT_FIELD = new Font ("Arial", Font.PLAIN, 16);
    protected static final Font FONT_BUTTON = new Font("Tahoma", Font.BOLD, 14);
    protected static final Font FONT_TITLE = new Font("Tahoma", Font.BOLD, 25);

    // Colors
    public static final Color BUTTON_BLUE = new Color(0, 123, 255);
    public static final Color BUTTON_GREEN = new Color(76, 175, 80);
    public static final Color BUTTON_GRAY =new Color(121, 128, 141);
    public static final Color BUTTON_RED = new Color(220, 53, 69);
    public static final Color DEFAULT_FOREGROUND = Color.WHITE;
    protected static final Color DEFAULT_BACKGROUND = Color.DARK_GRAY;

    // Borders
    public static Border BUTTON_RAISED =BorderFactory.createRaisedBevelBorder();

    protected JLabel title;
    protected JPanel content;

    public Page(String name) {
        super(new GridBagLayout());


        UIManager.put("Label.font", FONT_LABEL);
        UIManager.put("Label.foreground", DEFAULT_FOREGROUND);
        UIManager.put("Label.border", BorderFactory.createEmptyBorder());
        UIManager.put("Label.font", FONT_LABEL);

        UIManager.put("Button.font", FONT_LABEL);
        UIManager.put("Button.foreground", DEFAULT_FOREGROUND);
        UIManager.put("Button.border", BUTTON_RAISED);
        UIManager.put("Button.focusPainted", false);
        UIManager.put("Button.borderPainted", true);

        UIManager.put("TextComponent.font", FONT_FIELD);
        UIManager.put("TextComponent.foreground", DEFAULT_FOREGROUND);
        UIManager.put("TextComponent.caretForeground", DEFAULT_FOREGROUND);
        UIManager.put("TextComponent.opaque", false);

        UIManager.put("TextField.background", DEFAULT_BACKGROUND);
        UIManager.put("TextField.foreground", DEFAULT_FOREGROUND);
        UIManager.put("TextField.caretForeground", DEFAULT_FOREGROUND);
        UIManager.put("TextField.font", FONT_FIELD);

        UIManager.put("FormattedTextField.background", DEFAULT_BACKGROUND);
        UIManager.put("FormattedTextField.foreground", DEFAULT_FOREGROUND);
        UIManager.put("FormattedTextField.font", FONT_FIELD);
        UIManager.put("FormattedTextField.caretForeground", DEFAULT_FOREGROUND);

        UIManager.put("PasswordField.background", DEFAULT_BACKGROUND);
        UIManager.put("PasswordField.foreground", DEFAULT_FOREGROUND);
        UIManager.put("PasswordField.caretForeground", DEFAULT_FOREGROUND);
        UIManager.put("PasswordField.font", FONT_FIELD);

        UIManager.put("Panel.background", DEFAULT_BACKGROUND);
        UIManager.put("Panel.foreground", DEFAULT_FOREGROUND);

        UIManager.put("ComboBox.focus", BorderFactory.createEmptyBorder());
        UIManager.put("ComboBox.background", DEFAULT_BACKGROUND);
        UIManager.put("ComboBox.foreground", DEFAULT_FOREGROUND);
        UIManager.put("ComboBox.selectionBackground", DEFAULT_BACKGROUND.darker());
        UIManager.put("ComboBox.selectionForeground", DEFAULT_FOREGROUND);
        UIManager.put("ComboBox.font", FONT_FIELD);
        UIManager.put("ComboBox.border", BorderFactory.createEmptyBorder());
        UIManager.put("ComboBox.buttonBackground", DEFAULT_BACKGROUND);
        UIManager.put("ComboBox.buttonShadow", DEFAULT_BACKGROUND.darker());
        UIManager.put("ComboBox.buttonDarkShadow", DEFAULT_BACKGROUND.darker().darker());
        UIManager.put("ComboBox.buttonHighlight", DEFAULT_BACKGROUND);

        UIManager.put("Slider.background", DEFAULT_BACKGROUND);
        UIManager.put("Slider.foreground", DEFAULT_FOREGROUND);
        UIManager.put("Slider.trackWidth", Integer.valueOf(7));

        UIManager.put("Table.font", FONT_FIELD);
        UIManager.put("Table.foreground", DEFAULT_FOREGROUND);
        UIManager.put("Table.background", DEFAULT_BACKGROUND);
        UIManager.put("Table.gridColor", DEFAULT_FOREGROUND);
        UIManager.put("Table.selectionBackground", BUTTON_BLUE);
        UIManager.put("Table.selectionForeground", DEFAULT_FOREGROUND);
        UIManager.put("TableHeader.font", new Font("Arial", Font.BOLD, 18));
        UIManager.put("TableHeader.foreground", DEFAULT_FOREGROUND);
        UIManager.put("TableHeader.background", DEFAULT_BACKGROUND);
        UIManager.put("Viewport.background", DEFAULT_BACKGROUND);
        UIManager.put("Viewport.foreground", DEFAULT_FOREGROUND);

        UIManager.put("ScrollPane.background", DEFAULT_BACKGROUND);
        UIManager.put("ScrollPane.foreground", DEFAULT_FOREGROUND);
        UIManager.put("ScrollPane.border", BorderFactory.createEmptyBorder());
        UIManager.put("ScrollPane.viewportBorder", BorderFactory.createEmptyBorder());
        UIManager.put("ScrollBar.thumb", BUTTON_GRAY);
        UIManager.put("ScrollBar.track", DEFAULT_BACKGROUND);
        UIManager.put("ScrollBar.thumbDarkShadow", DEFAULT_BACKGROUND);
        UIManager.put("ScrollBar.thumbHighlight", DEFAULT_BACKGROUND);
        UIManager.put("ScrollBar.thumbShadow", DEFAULT_BACKGROUND);
        UIManager.put("ScrollBar.width", Integer.valueOf(18));


        this.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        title = new JLabel(name, SwingConstants.CENTER);
        title.setFont(FONT_TITLE);
        GridBagConstraints titleGBC = createGBC(
            0, 0, 
            GridBagConstraints.BOTH,
            1, 0,
            new Insets(0, 0, 10, 0));
        titleGBC.anchor = GridBagConstraints.NORTH;
        this.add(title, titleGBC);
        
        content = new JPanel(new GridBagLayout());
        populateContent();
        GridBagConstraints contentGBC = createGBC(
            0, 1, 
            GridBagConstraints.BOTH,
            1, 1,
            new Insets(0, 0, 0, 0));
        contentGBC.anchor = GridBagConstraints.NORTH;
        this.add(content, contentGBC);

        this.revalidate();
        this.repaint();
    }

    protected abstract void populateContent();

    protected GridBagConstraints createGBC(int column, int row, int fill, double weightx, double weighty, Insets insets) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = column;
        gbc.gridy = row;
        gbc.fill = fill;
        gbc.weightx = weightx;
        gbc.weighty = weighty;
        gbc.insets = insets;
        return gbc;
    }

    protected JButton createButton(String text, Color color, ActionListener action){
        JButton btn = new JButton(text);
        btn.addActionListener(action);
        btn.setBackground(color);
        return btn;
    }

    protected JLabel createInfoLabel(String text) {
        JLabel infoLabel = new JLabel(text);
        GridBagConstraints infoLabelGBC = createGBC(
            0, 2, 
            GridBagConstraints.BOTH,
            1, 0,
            new Insets(0, 0, 10, 0));
        infoLabelGBC.anchor = GridBagConstraints.WEST;
        this.add(infoLabel, infoLabelGBC);
        return infoLabel;
    }

    // Method to create a text entry panel
    public static JPanel createFieldPanel(String name, Boolean required, JTextComponent component) throws IllegalComponentStateException{

        // Create a label for the field
        String asterisk = required ? "<font color='red'>* </font>" : "";
        JLabel label = new JLabel("<html>" + asterisk + name + "</html>");

        // Create a panel to encapsulate the field and its label
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(label, BorderLayout.NORTH);
        panel.add(component, BorderLayout.CENTER);
        
        // Set default text field properties
        Border paddingBorder = BorderFactory.createEmptyBorder(10, 0, 0, 0);

        // Modify text field depending on its type
        if (component instanceof JPasswordField || component instanceof JTextField) {
            Border originalBorder = new MatteBorder(0, 0, 1, 0, Color.WHITE);
            component.setBorder(BorderFactory.createCompoundBorder(originalBorder, paddingBorder));

        } else if (component instanceof JTextPane){
            Border originalBorder = new LineBorder(DEFAULT_FOREGROUND) {
                public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
                    Graphics2D g2d = (Graphics2D)g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setColor(getLineColor());
                    g2d.drawRoundRect(x, y, width-1, height-1, 15, 15);
                }
            };
            component.setBorder(BorderFactory.createCompoundBorder(originalBorder, paddingBorder));
            component.setOpaque(false);
            component.setBackground(new Color(0, 103, 235));

        } else {
            throw new IllegalArgumentException("UIUtils.beautifyField does not support component type: " + component.getClass().getName());
        }
        return panel;
    }

    public JPanel createTempFieldPanel(String placeholder, JTextField textField) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(textField, BorderLayout.CENTER);

        Border paddingBorder = BorderFactory.createEmptyBorder(10, 10, 1, 10);
        Border originalBorder = new MatteBorder(0, 0, 1, 0, Color.WHITE);
        textField.setBorder(BorderFactory.createCompoundBorder(originalBorder, paddingBorder));

        textField.setForeground(Color.LIGHT_GRAY);
        textField.setText(placeholder);
        textField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                    textField.setForeground(DEFAULT_FOREGROUND);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    textField.setForeground(Color.LIGHT_GRAY);
                    textField.setText(placeholder);
                }
            }
        });

        return panel;
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
}