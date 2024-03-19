package pages.utils;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.*;


import java.awt.*;
import java.awt.event.*;
import java.text.ParseException;
import static pages.utils.UISettings.*;

public abstract class Page extends JPanel {  

    protected final JLabel title;
    protected final JPanel content;

    public Page(String name) {
        super(new GridBagLayout());

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
        btn.setForeground(DEFAULT_FOREGROUND);  // Set the foreground color to white

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

    public JPanel createTempFieldPanel(String name, String placeholder, JTextField textField) {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel label = new JLabel(name);
        label.setFont(new Font ("Arial", Font.BOLD, 20));
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.PAGE_END;
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(textField, gbc);

        Border paddingBorder = BorderFactory.createEmptyBorder(5, 10, 1, 0);
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

    protected JPanel doublePanel(JComponent item1, JComponent item2) {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = createGBC(
            0, 0, 
            GridBagConstraints.BOTH,
            0.5, 1,
            new Insets(0, 0, 0, 10));
        gbc.anchor = GridBagConstraints.PAGE_END;
        panel.add(item1, gbc);
        gbc.gridx = 1;
        gbc.insets = new Insets(0, 10, 0, 0);
        panel.add(item2, gbc);
        return panel;
    }
}