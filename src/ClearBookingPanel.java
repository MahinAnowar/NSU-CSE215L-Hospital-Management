import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;


public class ClearBookingPanel extends BasePanel {

    private JList<Booking> bookingList;
    private DefaultListModel<Booking> listModel;

    public ClearBookingPanel(JFrame mainFrame, DataManager dataManager) {
        super(mainFrame, dataManager);
        initializeComponents();
        loadBookings();
    }

    @Override
    protected void initializeComponents() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0;

        JLabel titleLabel = UIUtils.createBoldCenteredLabel("Clear Active Bookings");
        gbc.gridx=0; gbc.gridy=0; gbc.weighty=0; gbc.fill = GridBagConstraints.HORIZONTAL;
        addComponent(titleLabel, gbc);

        listModel = new DefaultListModel<>();
        bookingList = new JList<>(listModel);
        bookingList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bookingList.setFont(bookingList.getFont().deriveFont(Font.PLAIN));
        bookingList.setVisibleRowCount(15);
        JScrollPane scrollPane = new JScrollPane(bookingList);
        gbc.gridx=0; gbc.gridy=1; gbc.weighty=1.0;
        addComponent(scrollPane, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5)); // Button gaps
        buttonPanel.setOpaque(false);
        JButton clearButton = UIUtils.createStandardButton("Clear Selected");
        JButton clearAllButton = UIUtils.createStandardButton("Clear All");
        JButton backButton = UIUtils.createStandardButton("Back");
        buttonPanel.add(clearButton);
        buttonPanel.add(clearAllButton);
        buttonPanel.add(backButton);

        gbc.gridx=0; gbc.gridy=2; gbc.weighty=0; gbc.fill = GridBagConstraints.HORIZONTAL;
        addComponent(buttonPanel, gbc);

        // Action Listeners
        clearButton.addActionListener(e -> clearSelectedBooking());
        clearAllButton.addActionListener(e -> clearAllBookings());
        backButton.addActionListener(e -> switchPanel(new AdminPanel(mainFrame, dataManager))); // AdminPanel is local
    }

    private void loadBookings() {
        listModel.clear();
        List<Booking> bookings = dataManager.getBookings();
        if (bookings.isEmpty()) {
            listModel.addElement(null);
            bookingList.setEnabled(false);

            bookingList.setCellRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if (!(value instanceof Booking)) {
                        label.setText("No active bookings found.");
                        label.setHorizontalAlignment(SwingConstants.CENTER);
                        label.setForeground(Color.GRAY);
                        label.setEnabled(false);
                    } else {

                        label.setText(value.toString());
                        label.setHorizontalAlignment(SwingConstants.LEFT);
                        label.setForeground(list.getForeground());
                        label.setEnabled(list.isEnabled());
                    }
                    return label;
                }
            });
        } else {
            bookingList.setEnabled(true);
            bookingList.setCellRenderer(new DefaultListCellRenderer());
            bookings.forEach(listModel::addElement);
        }
    }

    private void clearSelectedBooking() {
        Booking selectedBooking = bookingList.getSelectedValue();

        if (selectedBooking == null || !(selectedBooking instanceof Booking)) {
            JOptionPane.showMessageDialog(mainFrame, "Please select a booking to clear.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(mainFrame,
                "Clear this booking?\n" + selectedBooking.toString() + "\nThe service will become available again.",
                "Confirm Clear", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                dataManager.clearBooking(selectedBooking);
                JOptionPane.showMessageDialog(mainFrame, "Booking cleared successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadBookings();
            } catch (Exception ex) {
                System.err.println("Error clearing booking: " + ex.getMessage());
                JOptionPane.showMessageDialog(mainFrame, "Error clearing booking: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                loadBookings();
            }
        }
    }

    private void clearAllBookings() {
        if (listModel.isEmpty() || !bookingList.isEnabled()) {
            JOptionPane.showMessageDialog(mainFrame, "There are no bookings to clear.", "No Bookings", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(mainFrame,
                "Are you sure you want to clear ALL active bookings?",
                "Confirm Clear All", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {

            List<Booking> bookingsToClear = new ArrayList<>();
            for (int i = 0; i < listModel.getSize(); i++) {
                Object item = listModel.getElementAt(i);
                if (item instanceof Booking) {
                    bookingsToClear.add((Booking) item);
                }
            }

            if (bookingsToClear.isEmpty()) {
                JOptionPane.showMessageDialog(mainFrame, "No valid bookings found in the list.", "Info", JOptionPane.INFORMATION_MESSAGE);
                loadBookings();
                return;
            }

            int clearedCount = 0;
            int errorCount = 0;
            try {
                for (Booking booking : bookingsToClear) {
                    try {
                        dataManager.clearBooking(booking);
                        clearedCount++;
                    } catch (Exception innerEx) {
                        System.err.println("Error clearing specific booking during clear all: " + booking + " - " + innerEx.getMessage());
                        errorCount++;
                    }
                }

                String message = String.format("%d booking(s) cleared successfully.", clearedCount);
                if (errorCount > 0) {
                    message += String.format("\n%d booking(s) could not be cleared.", errorCount);
                }
                JOptionPane.showMessageDialog(mainFrame, message, "Clear All Result",
                        (errorCount == 0) ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE);

                loadBookings();

            } catch (Exception ex) {
                System.err.println("Unexpected error during 'Clear All' operation: " + ex.getMessage());
                JOptionPane.showMessageDialog(mainFrame, "An unexpected error occurred during Clear All.", "Error", JOptionPane.ERROR_MESSAGE);
                loadBookings();
            }
        }
    }
}