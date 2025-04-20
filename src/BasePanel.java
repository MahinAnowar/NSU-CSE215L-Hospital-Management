import javax.swing.*;
import java.awt.*;


public abstract class BasePanel extends JPanel {
    protected JFrame mainFrame;
    protected DataManager dataManager;

    public BasePanel(JFrame mainFrame, DataManager dataManager) {
        this.mainFrame = mainFrame;
        this.dataManager = dataManager;
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    }


    protected void switchPanel(JPanel newPanel) {
        SwingUtilities.invokeLater(() -> {
            mainFrame.setContentPane(newPanel);
            mainFrame.revalidate();
            mainFrame.repaint();
            mainFrame.pack();
            UIUtils.centerFrame(mainFrame);
        });
    }


    protected abstract void initializeComponents();


    protected void addComponent(Component component, GridBagConstraints constraints) {
        add(component, constraints);
    }


    protected GridBagConstraints getDefaultConstraints() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.weightx = 1.0;
        gbc.weighty = 0;
        return gbc;
    }
}