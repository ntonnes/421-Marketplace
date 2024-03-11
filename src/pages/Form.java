package pages;

import java.awt.*;
import java.awt.event.ActionListener;
import java.text.ParseException;

import javax.swing.JFormattedTextField;
import javax.swing.text.JTextComponent;
import javax.swing.text.MaskFormatter;

public abstract class Form extends Page{
    protected static GridBagConstraints gbcL = Utils.makeGBC(0, 0, 0, 0);
    protected static GridBagConstraints gbcF = Utils.makeGBC(0, 0, 30, 0);
    protected static GridBagConstraints gbcB = Utils.makeGBC(0, 0, 10, 0);

    // Constructor
    public Form(String name) {
        super(name, new GridBagLayout());
    }


    // Method to add a label to the form
    protected static void addLabel(String text, Boolean required) {
        content.add(Utils.createLabel(text, Utils.arialB, false), gbcL);
    }

    // Method to add a text entry field to the form
    protected static void addTextField(JTextComponent field) {
        content.add(Utils.beautifyField(field, Utils.arial), gbcF);
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

    // Method to add a button to the form
    protected static void addButton(String text, Color color, ActionListener action) {
        content.add(Utils.styleButton(text, color, 0, 35, action), gbcB);
        
    }


    // Method to validate the form submission; must be implemented by the subclass
    protected abstract void submit();
}
