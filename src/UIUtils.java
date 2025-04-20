import javax.swing.*;
import java.awt.*;

public class UIUtils {

    public static JLabel createBoldCenteredLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 16f));
        return label;
    }

    public static JButton createStandardButton(String text) {
        JButton button = new JButton(text);
        button.setFont(button.getFont().deriveFont(Font.BOLD));
        button.setMargin(new Insets(10, 20, 10, 20));
        return button;
    }

    public static GridBagConstraints getConstraints(int gridx, int gridy, int gridwidth, double weighty) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = gridx;
        gbc.gridy = gridy;
        gbc.gridwidth = gridwidth;
        gbc.gridheight = 1;
        gbc.weightx = 1.0;
        gbc.weighty = weighty;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        return gbc;
    }

    public static GridBagConstraints getConstraints(int gridx, int gridy) {
        return getConstraints(gridx, gridy, 1, 0.0);
    }

    public static void centerFrame(Window frame) {
        if (frame.getWidth() == 0 || frame.getHeight() == 0) {
            System.err.println("Warning: Trying to center frame with zero size.");
            frame.pack();
            if (frame.getWidth() == 0 || frame.getHeight() == 0) return;
        }

        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);

        x = Math.max(0, x);
        y = Math.max(0, y);
        frame.setLocation(x, y);
    }
}