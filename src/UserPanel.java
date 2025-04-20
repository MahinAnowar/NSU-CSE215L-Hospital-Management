import javax.swing.*;
import java.awt.*;

public class UserPanel extends BasePanel {

    public UserPanel(JFrame mainFrame, DataManager dataManager) {
        super(mainFrame, dataManager);
        initializeComponents();
    }

    @Override
    protected void initializeComponents() {
        GridBagConstraints gbc = getDefaultConstraints();

        JLabel infoLabel = UIUtils.createBoldCenteredLabel("User Services");
        gbc.weighty = 0.1;
        addComponent(infoLabel, gbc);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setOpaque(false);

        JButton bookButton = UIUtils.createStandardButton("Book a Service");
        bookButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton backButton = UIUtils.createStandardButton("Back");
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        Dimension buttonSize = new Dimension(200, 40);
        bookButton.setPreferredSize(buttonSize);
        bookButton.setMaximumSize(buttonSize);
        backButton.setPreferredSize(buttonSize);
        backButton.setMaximumSize(buttonSize);

        buttonPanel.add(bookButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        buttonPanel.add(backButton);

        gbc.weighty = 0.9;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        addComponent(buttonPanel, gbc);


        // Action Listeners
        bookButton.addActionListener(e -> switchPanel(new SelectLocationPanel(mainFrame, dataManager)));
        backButton.addActionListener(e -> switchPanel(new MainPanel(mainFrame, dataManager)));
    }
}