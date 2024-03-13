package pages.forms;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.ParseException;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.text.JTextComponent;
import javax.swing.text.MaskFormatter;

import main.Main;
import pages.Page;
import pages.Utils;

public abstract class Form extends Page{
    protected int row = 0;  

    // Constructor
    public Form(Page lastPage, String name) {
        super(lastPage, name, new GridBagLayout());
    }


    // Method to add a label to the form
    protected void addLabel(String text, Boolean required) {
        addContent(Utils.createLabel(text, Utils.arialB, false), Utils.makeGbc(row++, 0, 0, 0, 0));
        row++;
    }

    // Method to add a text entry field to the form
    protected void addTextField(JTextComponent field) {
        addContent(Utils.beautifyField(field, Utils.arial), Utils.makeGbc(row++, 0, 0, 30, 0));
        row++;
    }

    // Method to add a button to the form
    protected void addButton(String text, Color color, ActionListener action) {
        addContent(Utils.styleButton(text, color, 0, 35, action), Utils.makeGbc(row++, 0, 0, 10, 0));
        row++;
    }

    protected void addHTML(String start, String link, String end) {
        JLabel htmlLabel = new JLabel("<html><body>" + start + "<a href='' style='color: #ADD8E6;'>" + link + "</a>" + end + "</body></html>");
        htmlLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        htmlLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { Main.goPage(new Signup()); }
        });
        addContent(htmlLabel, Utils.makeGbc(row++, 0, 0, 0, 0));
        row++;
    }

    // Method to add a formatted date entry field to the form
    protected static JFormattedTextField addDateField() {
        try {
            MaskFormatter dateMask = new MaskFormatter("##/##/####");
            dateMask.setPlaceholderCharacter('-');
            return new JFormattedTextField(dateMask);
        } catch (ParseException ex) {
            ex.printStackTrace();
            return null;
        }
    };


    // Method to validate the form submission; must be implemented by the subclass
    protected abstract void submit();
}
