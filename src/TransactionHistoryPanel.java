import javax.swing.*;
import java.awt.*;
import java.util.List;

public class TransactionHistoryPanel extends BasePanel {

    private JTextArea historyArea;

    public TransactionHistoryPanel(JFrame mainFrame, DataManager dataManager) {
        super(mainFrame, dataManager);
        initializeComponents();
        loadTransactions();
    }

    @Override
    protected void initializeComponents() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0;


        JLabel titleLabel = UIUtils.createBoldCenteredLabel("Pharmacy Transaction History");
        gbc.gridx=0; gbc.gridy=0; gbc.weighty=0; gbc.fill = GridBagConstraints.HORIZONTAL;
        addComponent(titleLabel, gbc);

        historyArea = new JTextArea();
        historyArea.setEditable(false);
        historyArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(historyArea);
        gbc.gridx=0; gbc.gridy=1; gbc.weighty=1.0;
        addComponent(scrollPane, gbc);


        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        JButton backButton = UIUtils.createStandardButton("Back");
        buttonPanel.add(backButton);
        gbc.gridx=0; gbc.gridy=2; gbc.weighty=0; gbc.fill = GridBagConstraints.HORIZONTAL;
        addComponent(buttonPanel, gbc);

        backButton.addActionListener(e -> switchPanel(new AdminPanel(mainFrame, dataManager)));
    }

    private void loadTransactions() {
        List<Transaction> transactions = dataManager.getTransactions();
        StringBuilder historyText = new StringBuilder();

        if (transactions.isEmpty()) {
            historyText.append("  No transaction history found.");
        } else {
            historyText.append("User Name - Phone - Medicine - Qty - Total Price (Hospital, Location)\n");
            historyText.append("-------------------------------------------------------------------------\n");
            for (Transaction tx : transactions) {
                historyText.append(tx.toString()).append("\n");
            }
        }

        historyArea.setText(historyText.toString());
        historyArea.setCaretPosition(0);
    }
}