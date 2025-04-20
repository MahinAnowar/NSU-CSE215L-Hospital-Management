import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

public class AddHospitalPanel extends BasePanel {

    private JComboBox<String> locationDropdown;
    private JTextField nameField;
    private JTextArea roomsArea;
    private JTextArea ambulancesArea;
    private JTextArea doctorsArea;
    private JTextArea pharmacyArea; // Format: Name:Price:Quantity,Name:Price:Quantity

    public AddHospitalPanel(JFrame mainFrame, DataManager dataManager) {
        super(mainFrame, dataManager);
        initializeComponents();
        loadLocations();
    }

    @Override
    protected void initializeComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Title
        JLabel titleLabel = UIUtils.createBoldCenteredLabel("Add New Hospital");
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER; gbc.fill = GridBagConstraints.NONE;
        gbc.weighty = 0;
        addComponent(titleLabel, gbc);
        gbc.anchor = GridBagConstraints.WEST;

        // Location
        gbc.gridx = 0; gbc.gridy++; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx=0;
        addComponent(new JLabel("Location:"), gbc);
        locationDropdown = new JComboBox<>();
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx=1.0;
        addComponent(locationDropdown, gbc);

        // Name
        gbc.gridx = 0; gbc.gridy++; gbc.fill = GridBagConstraints.NONE; gbc.weightx=0;
        addComponent(new JLabel("Hospital Name:"), gbc);
        nameField = new JTextField(25);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx=1.0;
        addComponent(nameField, gbc);

        // Rooms
        gbc.gridx = 0; gbc.gridy++; gbc.fill = GridBagConstraints.NONE; gbc.weightx=0; gbc.weighty=0;
        addComponent(new JLabel("Rooms (comma-sep):"), gbc);
        roomsArea = new JTextArea(2, 25);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weightx=1.0; gbc.weighty=0.1;
        addComponent(new JScrollPane(roomsArea), gbc);

        // Ambulances
        gbc.gridx = 0; gbc.gridy++; gbc.fill = GridBagConstraints.NONE; gbc.weightx=0; gbc.weighty=0;
        addComponent(new JLabel("Ambulances (comma-sep):"), gbc);
        ambulancesArea = new JTextArea(2, 25);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weightx=1.0; gbc.weighty=0.1;
        addComponent(new JScrollPane(ambulancesArea), gbc);

        // Doctors
        gbc.gridx = 0; gbc.gridy++; gbc.fill = GridBagConstraints.NONE; gbc.weightx=0; gbc.weighty=0;
        addComponent(new JLabel("Doctors (Name - Dept, comma-sep):"), gbc);
        doctorsArea = new JTextArea(3, 25);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weightx=1.0; gbc.weighty=0.2;
        addComponent(new JScrollPane(doctorsArea), gbc);

        // Pharmacy
        gbc.gridx = 0; gbc.gridy++; gbc.fill = GridBagConstraints.NONE; gbc.weightx=0; gbc.weighty=0;
        addComponent(new JLabel("Pharmacy (Name:Price:Qty, comma-sep):"), gbc);
        pharmacyArea = new JTextArea(4, 25);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weightx=1.0; gbc.weighty=0.4;
        addComponent(new JScrollPane(pharmacyArea), gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        buttonPanel.setOpaque(false);
        JButton saveButton = UIUtils.createStandardButton("Save Hospital");
        JButton backButton = UIUtils.createStandardButton("Back");
        buttonPanel.add(saveButton);
        buttonPanel.add(backButton);

        gbc.gridx = 0; gbc.gridy++; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER; gbc.weighty = 0; gbc.weightx=0;
        addComponent(buttonPanel, gbc);

        // Action Listeners
        saveButton.addActionListener(e -> saveHospital());
        backButton.addActionListener(e -> switchPanel(new AdminPanel(mainFrame, dataManager))); // Go back to AdminPanel
    }

    private void loadLocations() {
        List<String> locations = dataManager.getLocations();
        locationDropdown.setModel(new DefaultComboBoxModel<>(new Vector<>(locations)));
        if (!locations.isEmpty()) {
            locationDropdown.setSelectedIndex(0);
        }
    }

    private List<String> parseCommaSeparated(String text) {
        if (text == null || text.trim().isEmpty()) {
            return List.of();
        }
        return Arrays.stream(text.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    private void saveHospital() {
        String location = (String) locationDropdown.getSelectedItem();
        String name = nameField.getText().trim();

        // Validation
        if (location == null || location.isEmpty()) {
            JOptionPane.showMessageDialog(mainFrame, "Please select a location.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(mainFrame, "Please enter a hospital name.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (dataManager.getHospitalByNameAndLocation(name, location).isPresent()) {
            JOptionPane.showMessageDialog(mainFrame, "A hospital named '" + name + "' already exists in " + location + ".", "Duplicate Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<String> rooms = parseCommaSeparated(roomsArea.getText());
        List<String> ambulances = parseCommaSeparated(ambulancesArea.getText());
        List<String> doctors = parseCommaSeparated(doctorsArea.getText());

        List<Medicine> pharmacyStock = new Vector<>();
        String pharmacyText = pharmacyArea.getText().trim();
        boolean pharmacyParseError = false;
        if (!pharmacyText.isEmpty()) {
            String[] medEntries = pharmacyText.split(",");
            for (String entry : medEntries) {
                if (entry.trim().isEmpty()) continue;
                Medicine med = Medicine.fromFileString(entry.trim());
                if (med != null) {
                    pharmacyStock.add(med);
                } else {
                    System.err.println("Ignoring invalid pharmacy entry: " + entry.trim());
                    pharmacyParseError = true;
                }
            }
        }
        if (pharmacyParseError) {
            int choice = JOptionPane.showConfirmDialog(mainFrame,
                    "Some pharmacy entries were invalid and ignored.\nContinue saving?",
                    "Pharmacy Data Warning", JOptionPane.WARNING_MESSAGE, JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.NO_OPTION) return;
        }

        Hospital newHospital = new Hospital(location, name);
        newHospital.setAvailableRooms(rooms);
        newHospital.setAvailableAmbulances(ambulances);
        newHospital.setDoctors(doctors);
        newHospital.setPharmacyStock(pharmacyStock);

        try {
            dataManager.addHospital(newHospital);
            JOptionPane.showMessageDialog(mainFrame, "Hospital '" + name + "' added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            switchPanel(new AdminPanel(mainFrame, dataManager));
        } catch (Exception ex) {
            System.err.println("Error saving hospital: " + ex.getMessage());
            JOptionPane.showMessageDialog(mainFrame, "An error occurred while saving: " + ex.getMessage(), "Save Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}