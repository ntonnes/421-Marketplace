package pages;

import javax.swing.*;
import java.awt.*;


public abstract class ColumnPage extends Page {
    private int gridy = 0;
    protected static double YBUFFER = 1.0;
    protected static double XBUFFER = 0.35;
    protected static double XCONTENT = 0.3;
    protected static boolean showBorders = true;

    public ColumnPage(String title) {
        super(title);
    }

    protected void addComponent(JComponent component, double weightY) {
        GridBagConstraints gbc = createGBC(
            1, gridy,
            GridBagConstraints.BOTH,
            XCONTENT, weightY,
            new Insets(10, 0, 10, 0)
        );
        content.add(component, gbc);
        gridy++;
        YBUFFER-=weightY;
    } 

    protected void addBuffer(double weighty){
        GridBagConstraints gbc = createGBC(
            1, gridy,
            GridBagConstraints.BOTH,
            XCONTENT, weighty,
            new Insets(0, 0, 0, 0)
        );
        content.add(new JPanel(), gbc);
        gridy++;
        YBUFFER-=weighty;
    }

    protected void addBuffer() {
        GridBagConstraints verticalGBC = createGBC(
            1, gridy,
            GridBagConstraints.BOTH,
            XCONTENT, YBUFFER,
            new Insets(0, 0, 0, 0));
        content.add(new JPanel(), verticalGBC);
        gridy++;

        GridBagConstraints horizontalGBC = createGBC(
            0, gridy,
            GridBagConstraints.VERTICAL,
            XBUFFER, 1,
            new Insets(0, 0, 0, 0));
        content.add(new JPanel(), horizontalGBC);

        horizontalGBC.gridx = 2;
        content.add(new JPanel(), horizontalGBC);
    }
}