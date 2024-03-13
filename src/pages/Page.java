package pages;
import javax.swing.*;

import java.awt.*;

public abstract class Page extends JPanel {
    protected static Color BUTTON_BLUE = new Color(0, 123, 255);
    protected static Color BUTTON_GREEN = new Color(76, 175, 80);

    protected Page lastPage;
    protected JLabel title;
    protected JPanel content;

    public Page(Page lastPage, String name) {
        super(new GridBagLayout());
        this.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        this.lastPage = lastPage;

        GridBagConstraints gbc = UIUtils.createGBC(0, 0, 1, 0, GridBagConstraints.BOTH);
        gbc.anchor = GridBagConstraints.NORTH;
        this.add(UIUtils.createTitleLabel(name), gbc);
        
        content = new JPanel(new GridBagLayout());
        populateContent();
        gbc.gridy = 1;
        this.add(content, gbc);
    }

    public Page getLastPage() {
        return lastPage;
    }

    protected abstract void populateContent();

    public boolean isInstance(Page page) {
        return this.getClass().equals(page.getClass());
    }
}