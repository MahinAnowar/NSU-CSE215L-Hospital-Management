import javax.swing.*;
import java.awt.*;


public class HospitalServicesPanel extends BasePanel {

    private Hospital selectedHospital;

    public HospitalServicesPanel(JFrame mainFrame, DataManager dataManager, Hospital hospital) {
        super(mainFrame, dataManager);
        this.selectedHospital = hospital;
        if (selectedHospital == null) {
            JOptionPane.showMessageDialog(mainFrame, "Error: No hospital specified for services.", "Error", JOptionPane.ERROR_MESSAGE);

            SwingUtilities.invokeLater(() -> switchPanel(new SelectLocationPanel(mainFrame, dataManager)));
            return;
        }
        initializeComponents();
    }

    @Override
    protected void initializeComponents() {
        GridBagConstraints gbc = getDefaultConstraints();

        JLabel titleLabel = UIUtils.createBoldCenteredLabel("Welcome to " + selectedHospital.getName());
        gbc.weighty = 0.1;
        addComponent(titleLabel, gbc);

        JPanel buttonGridPanel = new JPanel(new GridLayout(3, 2, 15, 15));
        buttonGridPanel.setOpaque(false);

        JButton bookRoomButton = UIUtils.createStandardButton("Book a Room");
        JButton bookAmbulanceButton = UIUtils.createStandardButton("Book an Ambulance");
        JButton bookDoctorButton = UIUtils.createStandardButton("Appoint with a Doctor");
        JButton pharmacyButton = UIUtils.createStandardButton("Pharmacy");
        JButton backButton = UIUtils.createStandardButton("Back to Hospital List");

        buttonGridPanel.add(bookRoomButton);
        buttonGridPanel.add(bookAmbulanceButton);
        buttonGridPanel.add(bookDoctorButton);
        buttonGridPanel.add(pharmacyButton);

        buttonGridPanel.add(backButton);

        gbc.weighty = 0.8;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        addComponent(buttonGridPanel, gbc);


        bookRoomButton.addActionListener(e -> switchPanel(new BookServicePanel(mainFrame, dataManager, selectedHospital, "Room")));
        bookAmbulanceButton.addActionListener(e -> switchPanel(new BookServicePanel(mainFrame, dataManager, selectedHospital, "Ambulance")));
        bookDoctorButton.addActionListener(e -> switchPanel(new BookServicePanel(mainFrame, dataManager, selectedHospital, "Doctor")));
        pharmacyButton.addActionListener(e -> switchPanel(new PharmacyPanel(mainFrame, dataManager, selectedHospital)));
        backButton.addActionListener(e -> switchPanel(new SelectLocationPanel(mainFrame, dataManager)));
    }
}