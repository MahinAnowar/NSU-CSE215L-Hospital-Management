import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Vector;

public class SelectLocationPanel extends BasePanel {

    private JComboBox<String> locationDropdown;
    private JList<Hospital> hospitalList; // Hospital is local
    private DefaultListModel<Hospital> hospitalListModel;
    private JLabel statusLabel;

    public SelectLocationPanel(JFrame mainFrame, DataManager dataManager) {
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

        // Location Label and Dropdown
        JLabel locationLabel = UIUtils.createBoldCenteredLabel("Select Location:");
        locationLabel.setHorizontalAlignment(SwingConstants.LEFT);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 1; gbc.weighty = 0; gbc.anchor = GridBagConstraints.WEST;
        addComponent(locationLabel, gbc);

        locationDropdown = new JComboBox<>();
        locationDropdown.setFont(locationDropdown.getFont().deriveFont(Font.PLAIN));
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        addComponent(locationDropdown, gbc);

        // Status Label
        statusLabel = UIUtils.createBoldCenteredLabel("Select location to see hospitals.");
        statusLabel.setForeground(Color.BLUE);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        addComponent(statusLabel, gbc);

        // Hospital List
        hospitalListModel = new DefaultListModel<>();
        hospitalList = new JList<>(hospitalListModel);
        hospitalList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        hospitalList.setFont(hospitalList.getFont().deriveFont(Font.PLAIN));
        hospitalList.setVisibleRowCount(8);
        JScrollPane listScrollPane = new JScrollPane(hospitalList);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; gbc.weighty = 1.0; // Takes space
        gbc.fill = GridBagConstraints.BOTH;
        addComponent(listScrollPane, gbc);

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        buttonPanel.setOpaque(false);
        JButton selectButton = UIUtils.createStandardButton("Select Hospital");
        JButton backButton = UIUtils.createStandardButton("Back");
        buttonPanel.add(selectButton);
        buttonPanel.add(backButton);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2; gbc.weighty = 0; gbc.fill = GridBagConstraints.HORIZONTAL;
        addComponent(buttonPanel, gbc);

        locationDropdown.addActionListener(e -> updateHospitalList());
        selectButton.addActionListener(e -> selectHospitalAction());
        backButton.addActionListener(e -> switchPanel(new UserPanel(mainFrame, dataManager)));

        hospitalList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    selectHospitalAction();
                }
            }
        });
    }

    private void selectHospitalAction() {
        Hospital selectedHospital = hospitalList.getSelectedValue();
        if (selectedHospital != null) {
            switchPanel(new HospitalServicesPanel(mainFrame, dataManager, selectedHospital));
        } else {
            JOptionPane.showMessageDialog(mainFrame,
                    "Please select a hospital from the list.",
                    "Selection Required", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void loadLocations() {
        List<String> locations = dataManager.getLocations();
        locationDropdown.setModel(new DefaultComboBoxModel<>(new Vector<>(locations)));
        if (!locations.isEmpty()) {
            locationDropdown.setSelectedIndex(0);
            updateHospitalList();
        } else {
            statusLabel.setText("No locations available.");
            statusLabel.setForeground(Color.RED);
            hospitalListModel.clear();
            hospitalList.setEnabled(false);
        }
    }

    private void updateHospitalList() {
        hospitalListModel.clear();
        String selectedLocation = (String) locationDropdown.getSelectedItem();
        hospitalList.setEnabled(false);

        if (selectedLocation != null && !selectedLocation.isEmpty()) {
            List<Hospital> hospitalsInLocation = dataManager.getHospitalsByLocation(selectedLocation);

            if (hospitalsInLocation.isEmpty()) {
                statusLabel.setText("No hospitals found for " + selectedLocation + ".");
                statusLabel.setForeground(Color.RED);
            } else {
                statusLabel.setText("Select a hospital:");
                statusLabel.setForeground(Color.BLUE);
                hospitalsInLocation.forEach(hospitalListModel::addElement);
                hospitalList.setEnabled(true);
            }
        } else {
            statusLabel.setText("Please select a location.");
            statusLabel.setForeground(Color.RED);
        }
    }
}