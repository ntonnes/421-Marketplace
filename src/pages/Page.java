package pages;
import javax.swing.*;

import main.Main;

import java.awt.*;

public abstract class Page {
    public static Page currentPage;
    public Page previousPage;
    public Banner banner;
    public JPanel panel;
    public static JPanel content;
    public JLabel title;

    private static void switchPanel(JPanel oldPanel, JPanel newPanel, JFrame frame) {
        frame.getContentPane().remove(oldPanel);
        frame.getContentPane().add(newPanel, BorderLayout.CENTER);
        frame.revalidate();
        frame.repaint();
    }

    public static void goPrevPage() {
        switchPanel(currentPage.panel, currentPage.previousPage.panel, Main.getFrame());
        Page tmp = currentPage;
        currentPage = currentPage.previousPage;
        tmp.previousPage = null;
    }

    public Page(String name, LayoutManager layout) {
        previousPage = currentPage;
            
        panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.DARK_GRAY);

        banner = Main.banner;
                
        title = new JLabel(name, SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        content = new JPanel(layout);
        content.setBackground(Color.DARK_GRAY);
        populateContent();

        panel.add(banner.panel, BorderLayout.NORTH);
        panel.add(title, BorderLayout.CENTER); 
        panel.add(content, BorderLayout.SOUTH);
    }

    protected abstract void populateContent();

}