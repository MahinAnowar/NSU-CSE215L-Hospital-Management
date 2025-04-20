import javax.swing.*;
import java.awt.*;
import java.util.Arrays;


public class AdminLoginPanel extends BasePanel {

    private JPasswordField passwordField;
    private final String ADMIN_PASSWORD = "123";

    public AdminLoginPanel(JFrame mainFrame, DataManager dataManager) {
        super(mainFrame, dataManager);
        initializeComponents();
    }

    @Override
    protected void initializeComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel titleLabel = UIUtils.createBoldCenteredLabel("Admin Login");
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.weighty = 0.2;
        gbc.anchor = GridBagConstraints.PAGE_END;
        addComponent(titleLabel, gbc);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(passwordLabel.getFont().deriveFont(Font.BOLD));
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        gbc.weighty = 0;
        gbc.anchor = GridBagConstraints.LINE_END;
        gbc.fill = GridBagConstraints.NONE;
        addComponent(passwordLabel, gbc);

        passwordField = new JPasswordField(15);
        gbc.gridx = 1; gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        addComponent(passwordField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);
        JButton loginButton = UIUtils.createStandardButton("Login");
        JButton backButton = UIUtils.createStandardButton("Back");
        buttonPanel.add(loginButton);
        buttonPanel.add(backButton);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        gbc.weighty = 0.8;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.PAGE_START;
        addComponent(buttonPanel, gbc);

        loginButton.addActionListener(e -> performLogin());
        passwordField.addActionListener(e -> performLogin());
        backButton.addActionListener(e -> switchPanel(new MainPanel(mainFrame, dataManager)));
    }

    private void performLogin() {
        char[] enteredPassword = passwordField.getPassword();
        if (Arrays.equals(enteredPassword, ADMIN_PASSWORD.toCharArray())) {
            Arrays.fill(enteredPassword, '0');
            passwordField.setText("");
            switchPanel(new AdminPanel(mainFrame, dataManager));
        } else {
            Arrays.fill(enteredPassword, '0');
            passwordField.setText("");
            JOptionPane.showMessageDialog(mainFrame,
                    "Incorrect password. Please try again.",
                    "Login Failed", JOptionPane.ERROR_MESSAGE);
            passwordField.requestFocusInWindow();
        }
    }
}