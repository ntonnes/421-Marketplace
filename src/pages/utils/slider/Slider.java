package pages.utils.slider;

import javax.swing.*;
import java.awt.*;

public class Slider extends JPanel {
    private RangeSlider rSlider;
    private JLabel minimum;
    private JLabel maximum;

    public Slider(RangeSlider rangeSlider, String label, String prefix, int min, int max) {
        super(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        this.rSlider = rangeSlider;
        rSlider.setValue(min);
        rSlider.setUpperValue(max);

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font ("Arial", Font.BOLD, 20));
        lbl.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.PAGE_START;
        gbc.weighty = 1;
        this.add(lbl, gbc);

        minimum = new JLabel(prefix + rSlider.getValue());
        minimum.setBorder(BorderFactory.createEmptyBorder(0, 7, 0, 0));
        minimum.setFont(new Font ("Arial", Font.PLAIN, 16));
        gbc.gridy = 0;
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.SOUTHWEST;
        gbc.weighty = 0;
        this.add(minimum, gbc);

        maximum = new JLabel(prefix + rSlider.getUpperValue());
        maximum.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 7));
        maximum.setFont(new Font ("Arial", Font.PLAIN, 16));
        gbc.gridx = 2;
        gbc.anchor = GridBagConstraints.SOUTHEAST;
        this.add(maximum, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 2;
        gbc.weightx = 1;
        gbc.weighty = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 0, 0, 0);
        this.add(rSlider, gbc);

        rSlider.addChangeListener(e -> {
            minimum.setText(prefix + rSlider.getValue());
            maximum.setText(prefix + rSlider.getUpperValue());
        });
    }
}