package pages;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.*;


import java.awt.*;
import java.awt.event.*;
import java.text.ParseException;

public abstract class Page extends JPanel {

    // Fonts
    protected static final Font FONT_LABEL = new Font("Arial", Font.BOLD, 16);
    protected static final Font FONT_FIELD = new Font ("Arial", Font.PLAIN, 14);
    protected static final Font FONT_BUTTON = new Font("Tahoma", Font.BOLD, 14);
    protected static final Font FONT_TITLE = new Font("Tahoma", Font.BOLD, 25);

    // Colors
    protected static final Color BUTTON_BLUE = new Color(0, 123, 255);
    protected static final Color BUTTON_GREEN = new Color(76, 175, 80);
    protected static final Color BUTTON_GRAY =new Color(121, 128, 141);
    protected static final Color DEFAULT_FOREGROUND = Color.WHITE;

    // Borders
    protected static Border BUTTON_RAISED =BorderFactory.createRaisedBevelBorder();

    protected JLabel title;
    protected JPanel content;

    public Page(String name) {
        super(new GridBagLayout());
        this.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        title = new JLabel(name, SwingConstants.CENTER);
        title.setFont(FONT_TITLE);
        title.setForeground(DEFAULT_FOREGROUND);
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
        btn.setFont(FONT_LABEL);
        btn.setForeground(DEFAULT_FOREGROUND);
        btn.setBackground(color);
        btn.setBorder(BUTTON_RAISED);
        btn.setBorderPainted(true);
        btn.setFocusPainted(false);
        return btn;
    }

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