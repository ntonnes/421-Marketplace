package pages;
import javax.swing.*;

import java.awt.*;

public abstract class Page extends JPanel {
    protected static Color BUTTON_BLUE = new Color(0, 123, 255);
    protected static Color BUTTON_GREEN = new Color(76, 175, 80);

    protected Page lastPage;
    protected JLabel title;
    protected JPanel content;

    public Page(Page lastPage, String name, LayoutManager layout) {
        super(new BorderLayout());
        this.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        this.lastPage = lastPage;
        this.content = new JPanel(layout);

        this.add(Utils.createTitle(name), BorderLayout.NORTH);
        populateContent();
        this.add(content, BorderLayout.CENTER);
    }

    public Page getLastPage() {
        return lastPage;
    }

    protected abstract void populateContent();

    protected void addContent(JComponent component, Object constraints) {
        content.add(component, constraints);
    }
}