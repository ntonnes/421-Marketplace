package pages.utils;

import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.UIManager;
import javax.swing.border.Border;

public class UISettings {

    // Fonts
    public static final Font FONT_LABEL = new Font("Arial", Font.BOLD, 16);
    public static final Font FONT_FIELD = new Font ("Arial", Font.PLAIN, 16);
    public static final Font FONT_BUTTON = new Font("Tahoma", Font.BOLD, 14);
    public static final Font FONT_TITLE = new Font("Tahoma", Font.BOLD, 25);

    // Colors
    public static final Color BUTTON_BLUE = new Color(0, 123, 255);
    public static final Color BUTTON_GREEN = new Color(76, 175, 80);
    public static final Color BUTTON_GRAY =new Color(101, 108, 121);
    public static final Color BUTTON_RED = new Color(220, 53, 69);
    public static final Color DEFAULT_FOREGROUND = Color.WHITE;
    public static final Color DEFAULT_BACKGROUND = Color.DARK_GRAY;

    // Borders
    public static Border BUTTON_RAISED =BorderFactory.createRaisedBevelBorder();

    public static void installDefaults(){
        UIManager.put("Label.font", FONT_LABEL);
        UIManager.put("Label.foreground", DEFAULT_FOREGROUND);
        UIManager.put("Label.background", DEFAULT_BACKGROUND);
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
        UIManager.put("ComboBox.buttonShadow", DEFAULT_BACKGROUND);
        UIManager.put("ComboBox.buttonDarkShadow", DEFAULT_BACKGROUND);
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
    }
}
