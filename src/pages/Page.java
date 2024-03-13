package pages;
import javax.swing.*;

import java.awt.*;

public abstract class Page extends JPanel {
    protected static Color BUTTON_BLUE = new Color(0, 123, 255);
    protected static Color BUTTON_GREEN = new Color(76, 175, 80);

    protected Page lastPage;
    protected JLabel title;
    protected int row;
    protected JPanel banner;
    protected JPanel content;

    public Page(Page lastPage, String name) {
        super(new BorderLayout());
        this.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        this.banner = new JPanel(new GridBagLayout());
        this.content = new JPanel(new GridBagLayout());

        this.lastPage = lastPage;
        this.row = 0;

        GridBagConstraints gbc = UIUtils.createGBC(0, 0, 1, 1, GridBagConstraints.BOTH);
        gbc.fill = GridBagConstraints.BOTH;
        banner.add(UIUtils.createTitleLabel(name), UIUtils.createGBC(0,0,1,1, GridBagConstraints.BOTH));
        
        populateContent();
        this.add(banner, BorderLayout.NORTH);
        this.add(content, BorderLayout.CENTER);
    }

    public Page getLastPage() {
        return lastPage;
    }

    protected abstract void populateContent();

    public boolean isInstance(Page page) {
        return this.getClass().equals(page.getClass());
    }
}