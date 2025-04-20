import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Vector;

public class SelectHospitalForEditPanel extends BasePanel {

    private JComboBox<String> locationDropdown;
    private JList<Hospital> hospitalList;
    private DefaultListModel<Hospital> hospitalListModel;
    private JLabel statusLabel;

    public SelectHospitalForEditPanel(JFrame mainFrame, DataManager dataManager) {
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

        JLabel titleLabel = UIUtils.createBoldCenteredLabel("Select Hospital to Edit");
        gbc.gridx=0; gbc.gridy=0; gbc.gridwidth=2; gbc.fill=GridBagConstraints.NONE; gbc.weighty = 0;
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

        // Status
        statusLabel = UIUtils.createBoldCenteredLabel("Select location to see hospitals.");
        statusLabel.setForeground(Color.BLUE);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        addComponent(statusLabel, gbc);

        // List
        hospitalListModel = new DefaultListModel<>();
        hospitalList = new JList<>(hospitalListModel);
        hospitalList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        hospitalList.setFont(hospitalList.getFont().deriveFont(Font.PLAIN));
        hospitalList.setVisibleRowCount(8);
        JScrollPane listScrollPane = new JScrollPane(hospitalList);
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2; gbc.weighty = 1.0; gbc.fill = GridBagConstraints.BOTH;
        addComponent(listScrollPane, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        buttonPanel.setOpaque(false);
        JButton editButton = UIUtils.createStandardButton("Edit Selected");
        JButton backButton = UIUtils.createStandardButton("Back");
        buttonPanel.add(editButton);
        buttonPanel.add(backButton);

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2; gbc.weighty = 0; gbc.fill = GridBagConstraints.HORIZONTAL;
        addComponent(buttonPanel, gbc);


        locationDropdown.addActionListener(e -> updateHospitalList());

        editButton.addActionListener(e -> editSelectedHospital());


        hospitalList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    editSelectedHospital();
                }
            }
        });

        backButton.addActionListener(e -> switchPanel(new AdminPanel(mainFrame, dataManager)));
    }

    private void editSelectedHospital() {
        Hospital selectedHospital = hospitalList.getSelectedValue();
        if (selectedHospital != null) {
            switchPanel(new EditHospitalPanel(mainFrame, dataManager, selectedHospital));
        } else {
            JOptionPane.showMessageDialog(mainFrame, "Please select a hospital to edit.", "Selection Required", JOptionPane.WARNING_MESSAGE);
        }
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
                statusLabel.setText("Select a hospital to edit:");
                statusLabel.setForeground(Color.BLUE);
                hospitalsInLocation.forEach(hospitalListModel::addElement);
            }
        } else {
            statusLabel.setText("Please select a location.");
            statusLabel.setForeground(Color.RED);
        }
    }
}