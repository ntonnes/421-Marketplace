package pages;
import javax.swing.*;

import main.Main;

import java.awt.*;

public abstract class Page {
    public Page previousPage;
    public JPanel panel;
    public static JPanel content;
    public JLabel title;

    public static void goPage(Page newPage) {
        JFrame frame = Main.getFrame();
        Page cur = Main.currentPage;
        frame.getContentPane().remove(cur.panel);
        frame.getContentPane().add(newPage.panel, BorderLayout.CENTER);
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
        frame.revalidate();
        frame.repaint();
        Main.currentPage = cur.previousPage;
        cur.previousPage = null;
    }

    public Page(String name, LayoutManager layout) {

        panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.DARK_GRAY);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(Color.DARK_GRAY);

        title = new JLabel(name, SwingConstants.CENTER);
        title.setFont(new Font("Tahoma", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        content = new JPanel(layout);
        content.setBackground(Color.DARK_GRAY);
        populateContent();

        centerPanel.add(title, BorderLayout.NORTH);
        centerPanel.add(content, BorderLayout.CENTER);

        panel.add(centerPanel, BorderLayout.CENTER);
    }

    protected abstract void populateContent();
}