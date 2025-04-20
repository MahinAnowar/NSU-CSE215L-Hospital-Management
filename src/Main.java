import javax.swing.*;


public class Main {

    public static void main(String[] args) {

        DataManager dataManager = new DataManager();


        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Couldn't set system look and feel.");
        }

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Bangladesh Hospital Service");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


            MainPanel mainPanel = new MainPanel(frame, dataManager);

            frame.setContentPane(mainPanel);
            frame.pack();


            UIUtils.centerFrame(frame);

            frame.setVisible(true);
        });
    }
}