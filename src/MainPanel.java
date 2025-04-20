import javax.swing.*;
import java.awt.*;


public class MainPanel extends BasePanel {

    public MainPanel(JFrame mainFrame, DataManager dataManager) {
        super(mainFrame, dataManager);
        initializeComponents();
    }

    @Override
    protected void initializeComponents() {
        GridBagConstraints gbc = getDefaultConstraints();

        JLabel titleLabel = UIUtils.createBoldCenteredLabel("Welcome to the Bangladesh Hospital Service");
        gbc.weighty = 0.5;
        addComponent(titleLabel, gbc);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        buttonPanel.setOpaque(false);

        JButton userButton = UIUtils.createStandardButton("User");
        userButton.addActionListener(e -> switchPanel(new UserPanel(mainFrame, dataManager)));
        buttonPanel.add(userButton);

        JButton adminButton = UIUtils.createStandardButton("Admin");
        adminButton.addActionListener(e -> switchPanel(new AdminLoginPanel(mainFrame, dataManager)));
        buttonPanel.add(adminButton);

        gbc.weighty = 0.5;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.PAGE_START;
        addComponent(buttonPanel, gbc);
    }
}