import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class PharmacyPanel extends BasePanel {

    private Hospital hospital;
    private JList<Medicine> medicineList;
    private DefaultListModel<Medicine> listModel;
    private JTextField searchField;
    private JTextField quantityField;
    private JTextField nameField;
    private JTextField phoneField;
    private JLabel totalCostLabel;
    private List<Medicine> allMedicines;

    public PharmacyPanel(JFrame mainFrame, DataManager dataManager, Hospital hospital) {
        super(mainFrame, dataManager);
        if (hospital == null) {
            JOptionPane.showMessageDialog(mainFrame, "Error: No hospital specified for pharmacy.", "Error", JOptionPane.ERROR_MESSAGE);

            SwingUtilities.invokeLater(() -> switchPanel(new SelectLocationPanel(mainFrame, dataManager)));
            return;
        }
        this.hospital = hospital;

        this.allMedicines = new ArrayList<>(hospital.getPharmacyStock());
        initializeComponents();
        loadMedicines(this.allMedicines);
    }

    @Override
    protected void initializeComponents() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0;

        // Title
        JLabel title = UIUtils.createBoldCenteredLabel("Pharmacy at " + hospital.getName());
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 4; gbc.weighty = 0;
        addComponent(title, gbc);

        // Search Bar
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE; gbc.anchor = GridBagConstraints.EAST;
        addComponent(new JLabel("Search Medicine:"), gbc);
        searchField = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 1; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.anchor = GridBagConstraints.WEST;
        addComponent(searchField, gbc);

        // Medicine List
        listModel = new DefaultListModel<>();
        medicineList = new JList<>(listModel);
        medicineList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        medicineList.setFont(medicineList.getFont().deriveFont(Font.PLAIN));
        medicineList.setVisibleRowCount(8);
        JScrollPane listScrollPane = new JScrollPane(medicineList);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 4; gbc.weighty = 1.0; gbc.fill = GridBagConstraints.BOTH;
        addComponent(listScrollPane, gbc);



        JPanel purchasePanel = new JPanel(new GridBagLayout());
        purchasePanel.setOpaque(false);
        purchasePanel.setBorder(BorderFactory.createTitledBorder("Purchase Details"));
        GridBagConstraints ppGbc = new GridBagConstraints();
        ppGbc.insets = new Insets(3, 5, 3, 5);
        ppGbc.anchor = GridBagConstraints.WEST;


        ppGbc.gridx = 0; ppGbc.gridy = 0; ppGbc.fill = GridBagConstraints.NONE;
        purchasePanel.add(new JLabel("Quantity:"), ppGbc);
        quantityField = new JTextField(5);
        quantityField.setText("1"); // Default quantity
        ppGbc.gridx = 1; ppGbc.gridy = 0; ppGbc.fill = GridBagConstraints.HORIZONTAL; ppGbc.weightx=0.5;
        purchasePanel.add(quantityField, ppGbc);


        totalCostLabel = new JLabel("Total Cost: 0.00");
        totalCostLabel.setFont(totalCostLabel.getFont().deriveFont(Font.BOLD));
        ppGbc.gridx = 2; ppGbc.gridy = 0; ppGbc.fill = GridBagConstraints.HORIZONTAL; ppGbc.weightx=0.5; ppGbc.gridwidth=2;
        purchasePanel.add(totalCostLabel, ppGbc);


        ppGbc.gridx = 0; ppGbc.gridy = 1; ppGbc.fill = GridBagConstraints.NONE; ppGbc.gridwidth=1; ppGbc.weightx=0;
        purchasePanel.add(new JLabel("Your Name:"), ppGbc);
        nameField = new JTextField(15);
        ppGbc.gridx = 1; ppGbc.gridy = 1; ppGbc.fill = GridBagConstraints.HORIZONTAL; ppGbc.weightx=0.5;
        purchasePanel.add(nameField, ppGbc);


        ppGbc.gridx = 2; ppGbc.gridy = 1; ppGbc.fill = GridBagConstraints.NONE; ppGbc.weightx=0;
        purchasePanel.add(new JLabel("Your Phone:"), ppGbc);
        phoneField = new JTextField(15);
        ppGbc.gridx = 3; ppGbc.gridy = 1; ppGbc.fill = GridBagConstraints.HORIZONTAL; ppGbc.weightx=0.5;
        purchasePanel.add(phoneField, ppGbc);


        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 4; gbc.weighty = 0; gbc.fill = GridBagConstraints.HORIZONTAL;
        addComponent(purchasePanel, gbc);



        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        buttonPanel.setOpaque(false);
        JButton buyButton = UIUtils.createStandardButton("Buy Selected Medicine");
        JButton backButton = UIUtils.createStandardButton("Back");
        buttonPanel.add(buyButton);
        buttonPanel.add(backButton);


        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 4;
        addComponent(buttonPanel, gbc);



        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filterMedicines(); }
            public void removeUpdate(DocumentEvent e) { filterMedicines(); }
            public void changedUpdate(DocumentEvent e) { filterMedicines(); }
        });


        medicineList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) { // Prevent duplicate events on selection change
                updateTotalCost();
            }
        });


        quantityField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { updateTotalCost(); }
            public void removeUpdate(DocumentEvent e) { updateTotalCost(); }
            public void changedUpdate(DocumentEvent e) { updateTotalCost(); }
        });

        buyButton.addActionListener(e -> purchaseMedicine());
        backButton.addActionListener(e -> switchPanel(new HospitalServicesPanel(mainFrame, dataManager, hospital))); // HospitalServicesPanel is local
    }

    private void loadMedicines(List<Medicine> medicinesToLoad) {
        listModel.clear();
        if (medicinesToLoad == null || medicinesToLoad.isEmpty()) {

            listModel.addElement(null);
            medicineList.setEnabled(false);

            medicineList.setCellRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

                    JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if (!(value instanceof Medicine)) {
                        label.setText("No medicines found or available.");
                        label.setHorizontalAlignment(SwingConstants.CENTER);
                        label.setEnabled(false); // Greyed out
                        label.setForeground(Color.GRAY);
                    } else {

                        label.setText(value.toString());
                        label.setHorizontalAlignment(SwingConstants.LEFT);
                        label.setEnabled(list.isEnabled()); // Normal enabled state
                        label.setForeground(list.getForeground()); // Normal color
                    }

                    label.setOpaque(true);
                    if (isSelected) {
                        label.setBackground(list.getSelectionBackground());

                        label.setForeground(list.getSelectionForeground());
                    } else {
                        label.setBackground(list.getBackground());

                        if (!(value instanceof Medicine)) {
                            label.setForeground(Color.GRAY);
                        } else {
                            label.setForeground(list.getForeground());
                        }
                    }
                    return label;
                }
            });
        } else {

            medicineList.setEnabled(true);

            medicineList.setCellRenderer(new DefaultListCellRenderer());


            medicinesToLoad.stream()
                    .filter(m -> m.getQuantity() > 0)
                    .forEach(listModel::addElement);


            if (listModel.isEmpty()) {

                loadMedicines(List.of());
            }
        }

        updateTotalCost();
    }


    private void filterMedicines() {
        String searchTerm = searchField.getText().toLowerCase().trim();

        List<Medicine> sourceList = this.allMedicines != null ? this.allMedicines : List.of();

        if (searchTerm.isEmpty()) {
            loadMedicines(sourceList);
        } else {

            List<Medicine> filtered = sourceList.stream()
                    .filter(m -> m.getName().toLowerCase().contains(searchTerm))

                    .collect(Collectors.toList());
            loadMedicines(filtered);
        }
    }


    private void updateTotalCost() {
        Medicine selectedMedicine = medicineList.getSelectedValue();
        int quantity = 0;
        try {
            String qtyText = quantityField.getText().trim();
            if (!qtyText.isEmpty()) {
                quantity = Integer.parseInt(qtyText);
                if (quantity < 0) quantity = 0;
            }
        } catch (NumberFormatException e) {
            quantity = 0;
        }

        if (selectedMedicine != null && selectedMedicine instanceof Medicine) {
            double total = selectedMedicine.getPrice() * quantity;
            totalCostLabel.setText(String.format("Total Cost: %.2f", total));
        } else {
            totalCostLabel.setText("Total Cost: 0.00");
        }
    }

    private void purchaseMedicine() {
        Medicine selectedMedicine = medicineList.getSelectedValue();
        String userName = nameField.getText().trim();
        String userPhone = phoneField.getText().trim();
        int quantity;

        if (selectedMedicine == null || !(selectedMedicine instanceof Medicine)) {
            JOptionPane.showMessageDialog(mainFrame, "Please select a medicine from the list.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (userName.isEmpty() || userPhone.isEmpty()) {
            JOptionPane.showMessageDialog(mainFrame, "Please enter your name and phone number.", "Input Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!userPhone.matches("\\d+")) {
            JOptionPane.showMessageDialog(mainFrame, "Phone number should contain only digits.", "Invalid Input", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            String qtyText = quantityField.getText().trim();
            if (qtyText.isEmpty()) {
                JOptionPane.showMessageDialog(mainFrame, "Please enter quantity.", "Input Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            quantity = Integer.parseInt(qtyText);
            if (quantity <= 0) {
                JOptionPane.showMessageDialog(mainFrame, "Quantity must be greater than 0.", "Invalid Quantity", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(mainFrame, "Please enter a valid numeric quantity.", "Invalid Quantity", JOptionPane.WARNING_MESSAGE);
            return;
        }


        Medicine medicineInStock = hospital.findMedicine(selectedMedicine.getName());

        if (medicineInStock == null || medicineInStock.getQuantity() < quantity) {
            JOptionPane.showMessageDialog(mainFrame,
                    "Sorry, the requested quantity for " + selectedMedicine.getName() + " is not available.\nAvailable: " +
                            (medicineInStock != null ? medicineInStock.getQuantity() : 0),
                    "Insufficient Stock", JOptionPane.WARNING_MESSAGE);
            this.allMedicines = new ArrayList<>(hospital.getPharmacyStock());
            filterMedicines();
            return;
        }


        double totalPrice = medicineInStock.getPrice() * quantity;
        boolean purchaseSuccess = hospital.purchaseMedicine(medicineInStock.getName(), quantity);

        if (purchaseSuccess) {
            try {
                dataManager.updateHospital(hospital.getName(), hospital.getLocation(), hospital);

                Transaction transaction = new Transaction( // Transaction class is local
                        hospital.getName(), hospital.getLocation(), userName, userPhone,
                        medicineInStock.getName(), quantity, totalPrice);
                dataManager.addTransaction(transaction);

                JOptionPane.showMessageDialog(mainFrame,
                        "Congratulations! Purchased " + quantity + "x " + medicineInStock.getName() + " successfully.",
                        "Purchase Successful", JOptionPane.INFORMATION_MESSAGE);


                this.allMedicines = new ArrayList<>(hospital.getPharmacyStock());
                filterMedicines();
                nameField.setText("");
                phoneField.setText("");
                quantityField.setText("1");
                medicineList.clearSelection();
                updateTotalCost();

            } catch (Exception ex) {
                System.err.println("Error saving transaction/updating hospital: " + ex.getMessage());

                medicineInStock.setQuantity(medicineInStock.getQuantity() + quantity);
                JOptionPane.showMessageDialog(mainFrame,
                        "Purchase completed locally but failed to save permanently. Stock might be inaccurate. Please contact support.",
                        "Save Error", JOptionPane.ERROR_MESSAGE);
                this.allMedicines = new ArrayList<>(hospital.getPharmacyStock());
                filterMedicines();
            }
        } else {

            JOptionPane.showMessageDialog(mainFrame, "Purchase failed unexpectedly. The item might have become unavailable.", "Purchase Failed", JOptionPane.ERROR_MESSAGE);
            this.allMedicines = new ArrayList<>(hospital.getPharmacyStock());
            filterMedicines();
        }
    }
}