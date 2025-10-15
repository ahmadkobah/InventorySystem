
package pkgfinal.modeling;

import java.util.Scanner;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SingleMultiServerSimulation {

    public static void run() {
        try {
            String serverType = getStringInput("Choose server type (S for Single, M for Multi):");
            boolean isSingleServer = serverType.equalsIgnoreCase("S");

            int numCustomers = getIntInput("Enter number of customers:");

            int iatCount = getIntInput("Enter number of IAT values:");
            int[] iatValues = getIntArrayInput("Enter IAT values:", iatCount);

            double[] iatProbabilities = generateProbabilities("IAT", iatCount);
            displayTable("IAT Table", iatValues, iatProbabilities);

            int serviceTime1Count = getIntInput("Enter number of Service Time 1 values:");
            int[] serviceTimeValues1 = getIntArrayInput("Enter Service Time 1 values:", serviceTime1Count);

            double[] serviceTimeProbabilities1 = generateProbabilities("Service Time 1", serviceTime1Count);
            displayTable("Service Time 1 Table", serviceTimeValues1, serviceTimeProbabilities1);

            int[] serviceTimeValues2 = null;
            double[] serviceTimeProbabilities2 = null;

            if (!isSingleServer) {
                int serviceTime2Count = getIntInput("Enter number of Service Time 2 values:");
                serviceTimeValues2 = getIntArrayInput("Enter Service Time 2 values:", serviceTime2Count);
                serviceTimeProbabilities2 = generateProbabilities("Service Time 2", serviceTime2Count);
                displayTable("Service Time 2 Table", serviceTimeValues2, serviceTimeProbabilities2);
            }

            simulate(numCustomers, iatValues, iatProbabilities, serviceTimeValues1, serviceTimeProbabilities1, serviceTimeValues2, serviceTimeProbabilities2, isSingleServer);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: Invalid input. Please try again.");
        }
    }

    public static void displayTable(String tableName, int[] values, double[] probabilities) {
        List<String[]> rows = new ArrayList<>();
        double cumulative = 0.0;
        int rangeStart = 1;

        for (int i = 0; i < values.length; i++) {
            cumulative += probabilities[i] * 100;
            int rangeEnd = (int) Math.round(cumulative);
            rows.add(new String[] {
                String.valueOf(values[i]), 
                String.format("%.2f", probabilities[i]), 
                String.format("%.2f", cumulative / 100), 
                rangeStart + "-" + rangeEnd
            });
            rangeStart = rangeEnd + 1;
        }

        String[] columns = {"Value", "Probability", "Cumulative", "Range"};
        JTable table = new JTable(rows.toArray(new String[0][0]), columns);
        JScrollPane scrollPane = new JScrollPane(table);
        JFrame frame = new JFrame(tableName);
        frame.add(scrollPane);
        frame.setSize(600, 300);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }

    public static double[] generateProbabilities(String context, int length) {
        double[] probabilities = new double[length];
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
                break;
            case "2": // Same
                for (int i = 0; i < length; i++) {
                    probabilities[i] = 1.0 / length;
                }
                break;
            case "3": // Different
                double cumulativeSum = 0;
                for (int i = 0; i < length; i++) {
                    probabilities[i] = getDoubleInput("Enter probability for " + context + " value " + (i + 1) + ":");
                    cumulativeSum += probabilities[i];
                }
                break;
            default:
                JOptionPane.showMessageDialog(null, "Invalid choice. Defaulting to Same probabilities.");
                for (int i = 0; i < length; i++) {
                    probabilities[i] = 1.0 / length;
                }
        }
        return probabilities;
    }

    public static void simulate(int numCustomers, int[] iatValues, double[] iatProbabilities, int[] serviceTimeValues1, double[] serviceTimeProbabilities1, int[] serviceTimeValues2, double[] serviceTimeProbabilities2, boolean isSingleServer) {
          List<String[]> results = new ArrayList<>();
        int arrivalTime = 0;
        int machine1EndTime = 0;
        int machine2EndTime = 0;
        int totalWaitingTime = 0;
        int totalIdleTimeMachine1 = 0;
        int totalIdleTimeMachine2 = 0;

        for (int customer = 1; customer <= numCustomers; customer++) {
            int randomIAT = (int) (Math.random() * 100) + 1;
            int iat = generateRandomValue(randomIAT, iatValues, iatProbabilities);

            int randomService1 = (int) (Math.random() * 100) + 1;
            int serviceTime1 = generateRandomValue(randomService1, serviceTimeValues1, serviceTimeProbabilities1);

            arrivalTime += iat; 
            int serviceStartTime, serviceEndTime;
            Integer m1Start = null, m1End = null, m2Start = null, m2End = null;

            if (isSingleServer || machine1EndTime <= machine2EndTime) {
                serviceStartTime = Math.max(machine1EndTime, arrivalTime);
                serviceEndTime = serviceStartTime + serviceTime1;
                m1Start = serviceStartTime;
                m1End = serviceEndTime;
                totalIdleTimeMachine1 += Math.max(0, arrivalTime - machine1EndTime);
                machine1EndTime = serviceEndTime;
            } else {
                int randomService2 = (int) (Math.random() * 100) + 1;
                int serviceTime2 = generateRandomValue(randomService2, serviceTimeValues2, serviceTimeProbabilities2);
                serviceStartTime = Math.max(machine2EndTime, arrivalTime);
                serviceEndTime = serviceStartTime + serviceTime2;
                m2Start = serviceStartTime;
                m2End = serviceEndTime;
                totalIdleTimeMachine2 += Math.max(0, arrivalTime - machine2EndTime);
                machine2EndTime = serviceEndTime;
            }

            totalWaitingTime += serviceStartTime - arrivalTime;

            results.add(new String[] {
                String.valueOf(customer), 
                String.valueOf(randomIAT), 
                String.valueOf(iat), 
                String.valueOf(arrivalTime), 
                String.valueOf(randomService1), 
                String.valueOf(serviceTime1), 
                m1Start != null ? m1Start.toString() : "-", 
                m1End != null ? m1End.toString() : "-", 
                m2Start != null ? m2Start.toString() : "-", 
                m2End != null ? m2End.toString() : "-"
            });
        }

        results.add(new String[] { "", "", "", "", "Total Waiting Time", String.valueOf(totalWaitingTime), "", "", "", "" });
        results.add(new String[] { "", "", "", "", "Total Idle Time Machine 1", String.valueOf(totalIdleTimeMachine1), "", "", "", "" });
        results.add(new String[] { "", "", "", "", "Total Idle Time Machine 2", String.valueOf(totalIdleTimeMachine2), "", "", "", "" });

        JTable table = new JTable(results.toArray(new String[0][0]), new String[] {"Customer", "Rand IAT", "IAT", "Arrival", "Rand Srv", "Service", "M1 Start", "M1 End", "M2 Start", "M2 End"});
        JScrollPane scrollPane = new JScrollPane(table);
        JFrame frame = new JFrame("Single/Multi Server Simulation Results");
        frame.add(scrollPane);
        frame.setSize(1000, 500);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
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

    public static int[] getIntArrayInput(String message, int length) {
        int[] array = new int[length];
        for (int i = 0; i < length; i++) {
            array[i] = getIntInput(message + " (value " + (i + 1) + "):");
        }
        return array;
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

    public static int generateRandomValue(int randomValue, int[] values, double[] probabilities) {
        double cumulative = 0.0;
        for (int i = 0; i < probabilities.length; i++) {
            cumulative += probabilities[i];
            if (randomValue / 100.0 <= cumulative) return values[i];
        }
        return values[values.length - 1];
    }
}



