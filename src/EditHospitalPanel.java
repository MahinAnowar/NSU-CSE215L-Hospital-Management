import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;


public class EditHospitalPanel extends BasePanel {

    private Hospital hospitalToEdit;
    private String originalName;
    private String originalLocation;

    private JLabel locationLabelValue;
    private JTextField nameField;
    private JTextArea roomsArea;
    private JTextArea ambulancesArea;
    private JTextArea doctorsArea;
    private JTextArea pharmacyArea;

    public EditHospitalPanel(JFrame mainFrame, DataManager dataManager, Hospital hospital) {
        super(mainFrame, dataManager);
        if (hospital == null) {

            JOptionPane.showMessageDialog(mainFrame, "Error: Cannot edit a non-existent hospital.", "Error", JOptionPane.ERROR_MESSAGE);

            SwingUtilities.invokeLater(() -> switchPanel(new AdminPanel(mainFrame, dataManager)));

            return;
        }
        this.hospitalToEdit = hospital;
        this.originalName = hospital.getName();
        this.originalLocation = hospital.getLocation();
        initializeComponents();
        loadHospitalData();
    }

    @Override
    protected void initializeComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        JLabel titleLabel = UIUtils.createBoldCenteredLabel("Edit Hospital: " + originalName);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER; gbc.fill = GridBagConstraints.NONE;
        gbc.weighty = 0;
        addComponent(titleLabel, gbc);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy++; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx=0;
        addComponent(new JLabel("Location:"), gbc);
        locationLabelValue = new JLabel(originalLocation); // Display only
        locationLabelValue.setFont(locationLabelValue.getFont().deriveFont(Font.BOLD));
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx=1.0;
        addComponent(locationLabelValue, gbc);

        gbc.gridx = 0; gbc.gridy++; gbc.fill = GridBagConstraints.NONE; gbc.weightx=0;
        addComponent(new JLabel("Hospital Name:"), gbc);
        nameField = new JTextField(25);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx=1.0;
        addComponent(nameField, gbc);

        gbc.gridx = 0; gbc.gridy++; gbc.fill = GridBagConstraints.NONE; gbc.weightx=0; gbc.weighty=0;
        addComponent(new JLabel("Rooms (comma-sep):"), gbc);
        roomsArea = new JTextArea(2, 25);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weightx=1.0; gbc.weighty=0.1;
        addComponent(new JScrollPane(roomsArea), gbc);

        gbc.gridx = 0; gbc.gridy++; gbc.fill = GridBagConstraints.NONE; gbc.weightx=0; gbc.weighty=0;
        addComponent(new JLabel("Ambulances (comma-sep):"), gbc);
        ambulancesArea = new JTextArea(2, 25);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weightx=1.0; gbc.weighty=0.1;
        addComponent(new JScrollPane(ambulancesArea), gbc);

        gbc.gridx = 0; gbc.gridy++; gbc.fill = GridBagConstraints.NONE; gbc.weightx=0; gbc.weighty=0;
        addComponent(new JLabel("Doctors (Name - Dept, comma-sep):"), gbc);
        doctorsArea = new JTextArea(3, 25);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weightx=1.0; gbc.weighty=0.2;
        addComponent(new JScrollPane(doctorsArea), gbc);

        gbc.gridx = 0; gbc.gridy++; gbc.fill = GridBagConstraints.NONE; gbc.weightx=0; gbc.weighty=0;
        addComponent(new JLabel("Pharmacy (Name:Price:Qty, comma-sep):"), gbc);
        pharmacyArea = new JTextArea(4, 25);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weightx=1.0; gbc.weighty=0.4;
        addComponent(new JScrollPane(pharmacyArea), gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        buttonPanel.setOpaque(false);
        JButton saveButton = UIUtils.createStandardButton("Save Changes");
        JButton backButton = UIUtils.createStandardButton("Cancel");
        buttonPanel.add(saveButton);
        buttonPanel.add(backButton);

        gbc.gridx = 0; gbc.gridy++; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER; gbc.weighty = 0; gbc.weightx=0;
        addComponent(buttonPanel, gbc);

        saveButton.addActionListener(e -> saveChanges());
        backButton.addActionListener(e -> switchPanel(new SelectHospitalForEditPanel(mainFrame, dataManager))); // Go back to Select panel
    }

    private void loadHospitalData() {

        if (hospitalToEdit == null) return;

        nameField.setText(hospitalToEdit.getName());
        roomsArea.setText(String.join(", ", hospitalToEdit.getAvailableRooms()));
        ambulancesArea.setText(String.join(", ", hospitalToEdit.getAvailableAmbulances()));
        doctorsArea.setText(String.join(", ", hospitalToEdit.getDoctors()));

        String pharmacyText = hospitalToEdit.getPharmacyStock().stream()
                .map(Medicine::toFileString)
                .collect(Collectors.joining(", "));
        pharmacyArea.setText(pharmacyText);
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

    private void saveChanges() {
        if (hospitalToEdit == null) {
            JOptionPane.showMessageDialog(mainFrame, "Error: Cannot save null hospital.", "Save Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String newName = nameField.getText().trim();

        if (newName.isEmpty()) {
            JOptionPane.showMessageDialog(mainFrame, "Hospital name cannot be empty.", "Input Error", JOptionPane.ERROR_MESSAGE);
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
                    System.err.println("Ignoring invalid pharmacy entry during edit: " + entry.trim());
                    pharmacyParseError = true;
                }
            }
        }
        if (pharmacyParseError) {
            int choice = JOptionPane.showConfirmDialog(mainFrame,
                    "Some pharmacy entries were invalid and ignored.\nContinue saving?",
                    "Pharmacy Data Warning", JOptionPane.WARNING_MESSAGE, JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.NO_OPTION) {
                return;
            }
        }


        hospitalToEdit.setName(newName);

        hospitalToEdit.setAvailableRooms(rooms);
        hospitalToEdit.setAvailableAmbulances(ambulances);
        hospitalToEdit.setDoctors(doctors);
        hospitalToEdit.setPharmacyStock(pharmacyStock);

        try {

            dataManager.updateHospital(originalName, originalLocation, hospitalToEdit);
            JOptionPane.showMessageDialog(mainFrame, "Hospital '" + newName + "' updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            switchPanel(new SelectHospitalForEditPanel(mainFrame, dataManager));
        } catch (Exception ex) {
            System.err.println("Error updating hospital: " + ex.getMessage());
            JOptionPane.showMessageDialog(mainFrame, "Error updating hospital: " + ex.getMessage(), "Update Error", JOptionPane.ERROR_MESSAGE);

        }
    }
}