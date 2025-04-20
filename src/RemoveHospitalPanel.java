import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Vector;

public class RemoveHospitalPanel extends BasePanel {

    private JComboBox<String> locationDropdown;
    private JList<Hospital> hospitalList; // Hospital is local
    private DefaultListModel<Hospital> hospitalListModel;
    private JLabel statusLabel;

    public RemoveHospitalPanel(JFrame mainFrame, DataManager dataManager) {
        super(mainFrame, dataManager);
        initializeComponents();
        loadLocations();
    }

    @Override
    protected void initializeComponents() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0;

        JLabel titleLabel = UIUtils.createBoldCenteredLabel("Remove Hospital");
        gbc.gridx=0; gbc.gridy=0; gbc.gridwidth=2; gbc.fill=GridBagConstraints.NONE; gbc.weighty=0;
        addComponent(titleLabel, gbc);

        // Location
        JLabel locationLabel = UIUtils.createBoldCenteredLabel("Select Location:");
        locationLabel.setHorizontalAlignment(SwingConstants.LEFT);
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.anchor=GridBagConstraints.WEST;
        addComponent(locationLabel, gbc);

        locationDropdown = new JComboBox<>();
        locationDropdown.setFont(locationDropdown.getFont().deriveFont(Font.PLAIN));
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        addComponent(locationDropdown, gbc);

        statusLabel = UIUtils.createBoldCenteredLabel("Select location to see hospitals.");
        statusLabel.setForeground(Color.BLUE);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        addComponent(statusLabel, gbc);

        // Hospital List
        hospitalListModel = new DefaultListModel<>();
        hospitalList = new JList<>(hospitalListModel);
        hospitalList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        hospitalList.setFont(hospitalList.getFont().deriveFont(Font.PLAIN));
        hospitalList.setVisibleRowCount(8);
        JScrollPane listScrollPane = new JScrollPane(hospitalList);
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2; gbc.weighty = 1.0; // List takes space
        gbc.fill = GridBagConstraints.BOTH;
        addComponent(listScrollPane, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        buttonPanel.setOpaque(false);
        JButton removeButton = UIUtils.createStandardButton("Remove Selected");
        JButton backButton = UIUtils.createStandardButton("Back");
        buttonPanel.add(removeButton);
        buttonPanel.add(backButton);

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2; gbc.weighty = 0; gbc.fill = GridBagConstraints.HORIZONTAL;
        addComponent(buttonPanel, gbc);

        locationDropdown.addActionListener(e -> updateHospitalList());
        removeButton.addActionListener(e -> removeSelectedHospital());
        backButton.addActionListener(e -> switchPanel(new AdminPanel(mainFrame, dataManager)));
    }

    private void loadLocations() {
        List<String> locations = dataManager.getLocations();
        locationDropdown.setModel(new DefaultComboBoxModel<>(new Vector<>(locations)));
        if (!locations.isEmpty()) {
            locationDropdown.setSelectedIndex(0);
            updateHospitalList();
        } else {
            statusLabel.setText("No locations configured.");
            hospitalListModel.clear();
        }
    }

    private void updateHospitalList() {
        hospitalListModel.clear();
        String selectedLocation = (String) locationDropdown.getSelectedItem();
        if (selectedLocation != null) {
            List<Hospital> hospitalsInLocation = dataManager.getHospitalsByLocation(selectedLocation);
            if (hospitalsInLocation.isEmpty()) {
                statusLabel.setText("No hospitals found for " + selectedLocation + ".");
                statusLabel.setForeground(Color.RED);
            } else {
                statusLabel.setText("Select a hospital to remove:");
                statusLabel.setForeground(Color.BLUE);
                hospitalsInLocation.forEach(hospitalListModel::addElement);
            }
        } else {
            statusLabel.setText("Please select a location.");
            statusLabel.setForeground(Color.RED);
        }
    }

    private void removeSelectedHospital() {
        Hospital selectedHospital = hospitalList.getSelectedValue();
        if (selectedHospital == null) {
            JOptionPane.showMessageDialog(mainFrame, "Please select a hospital to remove.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        long relatedBookings = dataManager.getBookings().stream()
                .filter(b -> b.getHospitalName().equalsIgnoreCase(selectedHospital.getName()) &&
                        b.getLocation().equalsIgnoreCase(selectedHospital.getLocation()))
                .count();
        String warningMsg = "";
        if (relatedBookings > 0) {
            warningMsg = "\nWARNING: This hospital has " + relatedBookings + " active booking(s) which will NOT be cleared automatically.";
        }

        int confirm = JOptionPane.showConfirmDialog(mainFrame,
                "Remove '" + selectedHospital.getName() + "' from " + selectedHospital.getLocation() + "?\nThis cannot be undone." + warningMsg,
                "Confirm Removal", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                dataManager.removeHospital(selectedHospital);
                JOptionPane.showMessageDialog(mainFrame, "Hospital removed successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                updateHospitalList();
            } catch (Exception ex) {
                System.err.println("Error removing hospital: " + ex.getMessage());
                JOptionPane.showMessageDialog(mainFrame, "Error removing hospital: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}