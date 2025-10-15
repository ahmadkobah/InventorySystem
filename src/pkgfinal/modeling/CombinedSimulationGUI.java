package pkgfinal.modeling;

import javax.swing.*;
import java.awt.*;

public class CombinedSimulationGUI {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CombinedSimulationGUI().createAndShowGUI());
    }

    private void createAndShowGUI() {
        JFrame frame = new JFrame("Combined Simulation Project");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 350);
        frame.setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Select a Simulation to Run", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(5, 1, 10, 10));

        JButton singleMultiServerButton = new JButton("Single/Multi Server Simulation");
        singleMultiServerButton.addActionListener(e -> launchSingleMultiServerSimulation());

        JButton mnInventoryButton = new JButton("M/N Inventory Simulation");
        mnInventoryButton.addActionListener(e -> launchMNInventorySimulation());

        JButton newsVendorButton = new JButton("Parts Vendor Simulation");
        newsVendorButton.addActionListener(e -> launchNewsVendorSimulation());

        JButton eventSimulationButton = new JButton("Event Simulation");
        eventSimulationButton.addActionListener(e -> launchEventSimulation());

        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(e -> System.exit(0));

        buttonPanel.add(singleMultiServerButton);
        buttonPanel.add(mnInventoryButton);
        buttonPanel.add(newsVendorButton);
        buttonPanel.add(eventSimulationButton);
        buttonPanel.add(exitButton);

        mainPanel.add(buttonPanel, BorderLayout.CENTER);

        frame.add(mainPanel);
        frame.setVisible(true);
    }

    private void launchSingleMultiServerSimulation() {
        SingleMultiServerSimulation.run();
    }

    private void launchMNInventorySimulation() {
        MNInventorySimulation.run();
    }

    private void launchNewsVendorSimulation() {
        NewsVendorSimulation.run();
    }

    private void launchEventSimulation() {
        EventSimulation.main(new String[] {});
    }
}

