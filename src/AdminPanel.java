import javax.swing.*;
import java.awt.*;


public class AdminPanel extends BasePanel {

    public AdminPanel(JFrame mainFrame, DataManager dataManager) {
        super(mainFrame, dataManager);
        initializeComponents();
    }

    @Override
    protected void initializeComponents() {
        GridBagConstraints gbc = getDefaultConstraints();

        JLabel titleLabel = UIUtils.createBoldCenteredLabel("Admin Control Panel");
        gbc.weighty = 0.1;
        addComponent(titleLabel, gbc);

        // Button Grid Panel
        JPanel buttonGrid = new JPanel(new GridLayout(3, 2, 20, 20));
        buttonGrid.setOpaque(false);

        JButton addHospitalButton = UIUtils.createStandardButton("Add Hospital");
        JButton removeHospitalButton = UIUtils.createStandardButton("Remove Hospital");
        JButton editHospitalButton = UIUtils.createStandardButton("Edit Hospital");
        JButton clearBookingButton = UIUtils.createStandardButton("Clear Booking");
        JButton transactionHistoryButton = UIUtils.createStandardButton("Transaction History");
        JButton logoutButton = UIUtils.createStandardButton("Logout");

        buttonGrid.add(addHospitalButton);
        buttonGrid.add(removeHospitalButton);
        buttonGrid.add(editHospitalButton);
        buttonGrid.add(clearBookingButton);
        buttonGrid.add(transactionHistoryButton);
        buttonGrid.add(logoutButton);

        gbc.weighty = 0.9;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        addComponent(buttonGrid, gbc);

        // Action Listeners
        addHospitalButton.addActionListener(e -> switchPanel(new AddHospitalPanel(mainFrame, dataManager)));
        removeHospitalButton.addActionListener(e -> switchPanel(new RemoveHospitalPanel(mainFrame, dataManager)));
        editHospitalButton.addActionListener(e -> switchPanel(new SelectHospitalForEditPanel(mainFrame, dataManager)));
        clearBookingButton.addActionListener(e -> switchPanel(new ClearBookingPanel(mainFrame, dataManager)));
        transactionHistoryButton.addActionListener(e -> switchPanel(new TransactionHistoryPanel(mainFrame, dataManager)));
        logoutButton.addActionListener(e -> switchPanel(new MainPanel(mainFrame, dataManager)));
    }
}