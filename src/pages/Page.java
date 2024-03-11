package pages;
import javax.swing.*;

import main.Main;

import java.awt.*;

public abstract class Page {
    protected static Color BUTTON_BLUE = new Color(0, 123, 255);
    protected static Color BUTTON_GREEN = new Color(76, 175, 80);

    public Page previousPage;
    public JPanel panel;
    public static JPanel content;
    public JLabel title;

    public static void goPage(Page newPage) {
        JFrame frame = Main.getFrame();
        newPage.previousPage = Main.currentPage;
        frame.getContentPane().remove(Main.currentPage.panel);
        frame.getContentPane().add(newPage.panel, BorderLayout.CENTER);
        Main.currentPage = newPage;
        frame.revalidate();
        frame.repaint();
    }

    public static void goBack() {
        JFrame frame = Main.getFrame();
        Page cur = Main.currentPage;
        if (cur.previousPage == null){
            return;
        }
        frame.getContentPane().remove(cur.panel);
        frame.getContentPane().add(cur.previousPage.panel, BorderLayout.CENTER);
        Main.currentPage = cur.previousPage;
        cur.previousPage = null;
        frame.revalidate();
        frame.repaint();
    }

    public Page(String name, LayoutManager layout) {

        // The panel that holds the page itself (i.e. everything except the banner at the top)
        panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.DARK_GRAY);

        // The panel that holds the title and the content
        JPanel pagePanel = new JPanel(new BorderLayout());
        pagePanel.setBackground(Color.DARK_GRAY);

        // The title of the page
        title = new JLabel(name, SwingConstants.CENTER);
        title.setFont(new Font("Tahoma", Font.BOLD, 25));
        title.setForeground(Color.WHITE);
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 50, 0));

        // The content of the page
        content = new JPanel(layout);
        content.setBackground(Color.DARK_GRAY);
        populateContent();

        // Add the title and content to the pagePanel
        pagePanel.add(title, BorderLayout.NORTH);
        pagePanel.add(content, BorderLayout.CENTER);

        // Place the pagePanel in the NORTH region of the main panel
        // This ensures excess space is placed beneath the page content to avoid weird spacing
        panel.add(pagePanel, BorderLayout.NORTH);
    }

    protected abstract void populateContent();
}