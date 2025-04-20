import javax.swing.*;
import java.awt.*;
import java.util.ArrayList; // Added import
import java.util.List;


public class BookServicePanel extends BasePanel {

    private Hospital hospital; // Hospital class is local
    private String serviceType;
    private JList<String> serviceList;
    private DefaultListModel<String> listModel;
    private JTextField nameField;
    private JTextField phoneField;
    private JLabel titleLabel;

    public BookServicePanel(JFrame mainFrame, DataManager dataManager, Hospital hospital, String serviceType) {
        super(mainFrame, dataManager);
        this.hospital = hospital;
        this.serviceType = serviceType;
        initializeComponents();
        loadAvailableServices();
    }

    @Override
    protected void initializeComponents() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0;

        titleLabel = UIUtils.createBoldCenteredLabel("Book Available " + serviceType + "s at " + hospital.getName());
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.weighty = 0;
        addComponent(titleLabel, gbc);

        listModel = new DefaultListModel<>();
        serviceList = new JList<>(listModel);
        serviceList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        serviceList.setFont(serviceList.getFont().deriveFont(Font.PLAIN));
        serviceList.setVisibleRowCount(8);
        JScrollPane listScrollPane = new JScrollPane(serviceList);
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2; gbc.weighty = 1.0; // List takes space
        gbc.fill = GridBagConstraints.BOTH;
        addComponent(listScrollPane, gbc);

        // User Info Panel
        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        inputPanel.setOpaque(false);
        inputPanel.setBorder(BorderFactory.createTitledBorder("Your Information"));
        inputPanel.add(new JLabel("Your Name:"));
        nameField = new JTextField(20);
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Your Phone:"));
        phoneField = new JTextField(20);
        inputPanel.add(phoneField);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; gbc.weighty = 0; gbc.fill = GridBagConstraints.HORIZONTAL;
        addComponent(inputPanel, gbc);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        buttonPanel.setOpaque(false);
        JButton bookButton = UIUtils.createStandardButton("Book Selected " + serviceType);
        JButton backButton = UIUtils.createStandardButton("Back");
        buttonPanel.add(bookButton);
        buttonPanel.add(backButton);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        addComponent(buttonPanel, gbc);

        // Action Listeners
        bookButton.addActionListener(e -> bookService());
        backButton.addActionListener(e -> switchPanel(new HospitalServicesPanel(mainFrame, dataManager, hospital))); // HospitalServicesPanel is local
    }

    private void loadAvailableServices() {
        listModel.clear();
        List<String> availableItems = new ArrayList<>(); // Initialize explicitly
        switch (serviceType) {
            case "Room":
                availableItems = hospital.getAvailableRooms();
                break;
            case "Ambulance":
                availableItems = hospital.getAvailableAmbulances();
                break;
            case "Doctor":
                availableItems = hospital.getDoctors();
                break;
            default:
                System.err.println("Unknown service type: " + serviceType);
        }

        if (availableItems.isEmpty()) {
            listModel.addElement("No " + serviceType.toLowerCase() + "s currently available.");
            serviceList.setEnabled(false);
        } else {
            availableItems.forEach(listModel::addElement);
            serviceList.setEnabled(true);
        }
    }

    private void bookService() {
        String selectedService = serviceList.getSelectedValue();
        String userName = nameField.getText().trim();
        String userPhone = phoneField.getText().trim();

        if (selectedService == null || selectedService.startsWith("No ")) {
            JOptionPane.showMessageDialog(mainFrame, "Please select an available " + serviceType + " from the list.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (userName.isEmpty() || userPhone.isEmpty()) {
            JOptionPane.showMessageDialog(mainFrame, "Please enter your name and phone number.", "Input Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!userPhone.matches("\\d+")) {
            JOptionPane.showMessageDialog(mainFrame, "Please enter a valid phone number (digits only).", "Invalid Phone", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean successfullyRemovedLocally = false;
        switch (serviceType) {
            case "Room":
                successfullyRemovedLocally = hospital.bookRoom(selectedService);
                break;
            case "Ambulance":
                successfullyRemovedLocally = hospital.bookAmbulance(selectedService);
                break;
            case "Doctor":
                successfullyRemovedLocally = hospital.bookDoctor(selectedService);
                break;
        }

        if (successfullyRemovedLocally) {
            try {

                dataManager.updateHospital(hospital.getName(), hospital.getLocation(), hospital);

                Booking newBooking = new Booking(hospital.getName(), hospital.getLocation(), serviceType, selectedService, userName, userPhone); // Booking is local
                dataManager.addBooking(newBooking);

                JOptionPane.showMessageDialog(mainFrame,
                        serviceType + " '" + selectedService + "' booked successfully for " + userName + "!",
                        "Booking Confirmation", JOptionPane.INFORMATION_MESSAGE);

                loadAvailableServices(); // Refresh list
                nameField.setText("");
                phoneField.setText("");

            } catch (Exception ex) {
                System.err.println("Error saving booking/updating hospital: " + ex.getMessage());

                switch (serviceType) {
                    case "Room": hospital.returnRoom(selectedService); break;
                    case "Ambulance": hospital.returnAmbulance(selectedService); break;
                    case "Doctor": hospital.returnDoctor(selectedService); break;
                }
                JOptionPane.showMessageDialog(mainFrame, "An error occurred saving the booking. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                loadAvailableServices();
            }
        } else {
            JOptionPane.showMessageDialog(mainFrame,
                    "Sorry, the selected " + serviceType + " is no longer available. The list has been refreshed.",
                    "Booking Failed", JOptionPane.ERROR_MESSAGE);
            loadAvailableServices();
        }
    }
}