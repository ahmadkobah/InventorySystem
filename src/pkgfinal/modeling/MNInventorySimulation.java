package pkgfinal.modeling;

import java.util.Scanner;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MNInventorySimulation {
    
    public static void run() {
        try {
            int startInventory = getIntInput("Enter starting inventory:");
            int orderQuantity = getIntInput("Enter order quantity:");
            int orderCondition = getIntInput("Enter order condition (inventory level to reorder):");
            int numberOfWeeks = getIntInput("Enter number of weeks for the simulation:");
            int simulationDays = numberOfWeeks * 7;

            int demandMin = getIntInput("Enter minimum demand value:");
            int demandMax = getIntInput("Enter maximum demand value:");

            double[] demandProbabilities = generateProbabilities("Demand", demandMax - demandMin + 1);
            displayProbabilityTable("Demand Table", demandMin, demandMax, demandProbabilities);

            int leadTimeMin = getIntInput("Enter minimum lead time value:");
            int leadTimeMax = getIntInput("Enter maximum lead time value:");

            double[] leadTimeProbabilities = generateProbabilities("Lead Time", leadTimeMax - leadTimeMin + 1);
            displayProbabilityTable("Lead Time Table", leadTimeMin, leadTimeMax, leadTimeProbabilities);

            simulateInventory(startInventory, orderQuantity, orderCondition, simulationDays, demandMin, demandMax, demandProbabilities, leadTimeMin, leadTimeMax, leadTimeProbabilities, numberOfWeeks);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: Invalid input. Please try again.");
        }
    }

    public static void displayProbabilityTable(String tableName, int min, int max, double[] probabilities) {
        List<String[]> rows = new ArrayList<>();
        double cumulative = 0.0;
        int rangeStart = 1;
        for (int i = 0; i < probabilities.length; i++) {
            cumulative += probabilities[i] * 100;
            int rangeEnd = (int) Math.round(cumulative);
            rows.add(new String[] {
                String.valueOf(min + i), 
                String.format("%.2f", probabilities[i]), 
                String.format("%.2f", cumulative / 100), 
                rangeStart + "-" + rangeEnd
            });
            rangeStart = rangeEnd + 1;
        }

        String[] columns = {"Value", "Probability", "Cumulative", "Range"};
        JTable table = new JTable(rows.toArray(new String[0][0]), columns);
        displayTable(table, tableName);
    }

 public static void simulateInventory(int startInventory, int orderQuantity, int orderCondition, int simulationDays, int demandMin, int demandMax, double[] demandProbabilities, int leadTimeMin, int leadTimeMax, double[] leadTimeProbabilities, int numberOfWeeks) {
        List<String[]> results = new ArrayList<>();
        int inventory = startInventory;
        int orderArrivalDay = -1;
        int cycleLength = 7;
        int currentCycle = 1;
        int cumulativeShortage = 0;
        int totalShortage = 0;
        int totalOrdersPlaced = 0;
        int totalInventoryEnd = 0;

        for (int day = 1; day <= simulationDays; day++) {
            if ((day - 1) % cycleLength == 0) {
                currentCycle = (day - 1) / cycleLength + 1;
            }

            int beginInventory = inventory;

            if (day == orderArrivalDay) {
                inventory += orderQuantity;
                if (cumulativeShortage > 0) {
                    int usedToClearShortage = Math.min(inventory, cumulativeShortage);
                    cumulativeShortage -= usedToClearShortage;
                    inventory -= usedToClearShortage;
                }
                orderArrivalDay = -1;
            }

            int randomDemand = (int) (Math.random() * 100) + 1;
            int demand = generateRandomValue(randomDemand, demandMin, demandMax, demandProbabilities);

            int shortage = 0;
            if (inventory < demand) {
                shortage = demand - inventory;
                cumulativeShortage += shortage;
                totalShortage += shortage;
            }
            inventory = Math.max(0, inventory - demand);
            int endInventory = inventory;
            totalInventoryEnd += endInventory;

            boolean orderPlaced = false;
            int randomLeadTime = 0;
            int leadTime = 0;

            if (endInventory <= orderCondition && orderArrivalDay == -1) {
                randomLeadTime = (int) (Math.random() * 100) + 1;
                leadTime = generateRandomValue(randomLeadTime, leadTimeMin, leadTimeMax, leadTimeProbabilities);
                orderArrivalDay = day + leadTime;
                orderPlaced = true;
                totalOrdersPlaced++;
            }

            results.add(new String[]{
                String.valueOf(currentCycle), String.valueOf(day), String.valueOf(beginInventory),
                String.valueOf(randomDemand), String.valueOf(demand), String.valueOf(endInventory),
                String.valueOf(cumulativeShortage), orderPlaced ? "Yes" : "No", 
                orderPlaced ? String.valueOf(orderQuantity) : "-", 
                orderPlaced ? String.valueOf(randomLeadTime) : "-", 
                orderPlaced ? String.valueOf(leadTime) : "-"
            });
        }

        String[] columns = {"Cycle", "Day", "Begin Inventory", "Random Demand", "Demand", "End Inventory", "Shortage", "Order Placed", "Order Quantity", "Random Lead Time", "Days Until Order Arrive"};
        JTable table = new JTable(results.toArray(new String[0][0]), columns);
        displayTable(table, "M/N Inventory Simulation Results");

        String summary = "Final Results:\n" +
                         "Total Shortage: " + totalShortage + "\n" +
                         "Total Orders Placed: " + totalOrdersPlaced + "\n" +
                         "Total End Inventory: " + totalInventoryEnd + "\n";
        JOptionPane.showMessageDialog(null, summary, "Simulation Summary", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void displayTable(JTable table, String title) {
        JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(1200, 600);
        JScrollPane scrollPane = new JScrollPane(table);
        frame.add(scrollPane);
        frame.setVisible(true);
    }

    public static int getIntInput(String message) {
        while (true) {
            try {
                String input = JOptionPane.showInputDialog(message);
                if (input == null) exitProgram();
                return Integer.parseInt(input);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Invalid input. Please enter a valid number.");
            }
        }
    }

    public static double getDoubleInput(String message) {
        while (true) {
            try {
                String input = JOptionPane.showInputDialog(message);
                if (input == null) exitProgram();
                return Double.parseDouble(input);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Invalid input. Please enter a valid decimal number.");
            }
        }
    }

    public static String getStringInput(String message) {
        while (true) {
            try {
                String input = JOptionPane.showInputDialog(message);
                if (input == null) exitProgram();
                return input;
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Invalid input. Please enter a valid string.");
            }
        }
    }

    public static void exitProgram() {
        JOptionPane.showMessageDialog(null, "Process cancelled. Exiting...");
        System.exit(0);
    }
    
    public static double[] generateProbabilities(String context, int length) {
    double[] probabilities = new double[length];
    while (true) {
        String choice = getStringInput("Choose the type of probabilities for " + context + " (1 - Random, 2 - Same, 3 - Different):");
        switch (choice) {
            case "1": // Random
                double sum = 0;
                for (int i = 0; i < length; i++) {
                    probabilities[i] = Math.random();
                    sum += probabilities[i];
                }
                for (int i = 0; i < length; i++) {
                    probabilities[i] /= sum;
                }
                return probabilities;

            case "2": // Same
                for (int i = 0; i < length; i++) {
                    probabilities[i] = 1.0 / length;
                }
                return probabilities;

            case "3": // Different (User Input with validation)
                double cumulativeSum = 0;
                for (int i = 0; i < length; i++) {
                    probabilities[i] = getDoubleInput("Enter probability for " + context + " value " + (i + 1) + ":");
                    cumulativeSum += probabilities[i];
                    if (cumulativeSum > 1.0) {
                        JOptionPane.showMessageDialog(null, "Error: Cumulative probability exceeds 1. Please re-enter all probabilities.");
                        return generateProbabilities(context, length);
                    }
                }
                return probabilities;

            default:
                JOptionPane.showMessageDialog(null, "Invalid choice. Please enter 1, 2, or 3.");
        }
    }
}

    public static int generateRandomValue(int randomValue, int min, int max, double[] probabilities) {
        double cumulative = 0.0;
        for (int i = 0; i < probabilities.length; i++) {
            cumulative += probabilities[i] * 100;
            if (randomValue <= cumulative) {
                return min + i;
            }
        }
        return max;
    }
}




