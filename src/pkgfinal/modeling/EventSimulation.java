package pkgfinal.modeling;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;
import javax.swing.*;

public class EventSimulation {

    private static class Customer {
        int IAT;
        int ArrivalTime;
        int ServiceTime;
        int ServiceBegin;
        int ServiceEnd;
        int ServerIdle;
    }

    private static class Event {
        String type; // 'A' for Arrival, 'D' for Departure
        int time;
        int customerId;

        public Event(String type, int time, int customerId) {
            this.type = type;
            this.time = time;
            this.customerId = customerId;
        }
    }

    private static class EventComparator implements Comparator<Event> {
        @Override
        public int compare(Event e1, Event e2) {
            return Integer.compare(e1.time, e2.time);
        }
    }

    private ArrayList<Event> eventList;
    private ArrayList<Customer> customers;
    private int simulationLength;
    private Random random;
    private int[] iatValues;
    private double[] iatProbabilities;
    private int[] serviceTimeValues;
    private double[] serviceTimeProbabilities;

    public EventSimulation() {
        this.eventList = new ArrayList<>();
        this.customers = new ArrayList<>();
        this.random = new Random();
    }

    public void initializeSimulation() {
        inputData();
        for (int i = 0; i < simulationLength; i++) {
            Customer customer = new Customer();
            customer.IAT = generateRandomValue(iatValues, iatProbabilities);
            customer.ServiceTime = generateRandomValue(serviceTimeValues, serviceTimeProbabilities);
            customers.add(customer);
        }
        computeArrivalTimes();
        scheduleInitialEvents();
    }

    private void inputData() {
        simulationLength = getIntInput("Enter the number of customers:");

        int iatCount = getIntInput("Enter the number of IAT values:");
        iatValues = getIntArrayInput("Enter IAT values:", iatCount);
        iatProbabilities = generateProbabilities("IAT", iatCount);
        
        int serviceTimeCount = getIntInput("Enter the number of service time values:");
        serviceTimeValues = getIntArrayInput("Enter service time values:", serviceTimeCount);
        serviceTimeProbabilities = generateProbabilities("Service Time", serviceTimeCount);
    }

    private void computeArrivalTimes() {
        customers.get(0).ArrivalTime = customers.get(0).IAT;
        for (int i = 1; i < customers.size(); i++) {
            customers.get(i).ArrivalTime = customers.get(i - 1).ArrivalTime + customers.get(i).IAT;
        }
    }

    private void scheduleInitialEvents() {
        for (int i = 0; i < customers.size(); i++) {
            eventList.add(new Event("A", customers.get(i).ArrivalTime, i));
        }
        Collections.sort(eventList, new EventComparator());
    }

    public void simulate() {
        for (int i = 0; i < customers.size(); i++) {
            Customer customer = customers.get(i);
            customer.ServiceBegin = (i == 0) ? customer.ArrivalTime : Math.max(customer.ArrivalTime, customers.get(i - 1).ServiceEnd);
            customer.ServiceEnd = customer.ServiceBegin + customer.ServiceTime;
            customer.ServerIdle = (i == 0) ? customer.ArrivalTime : customer.ServiceBegin - customers.get(i - 1).ServiceEnd;

            eventList.add(new Event("D", customer.ServiceEnd, i));
            Collections.sort(eventList, new EventComparator());
        }
    }

    public int generateRandomValue(int[] values, double[] probabilities) {
        double randomValue = random.nextDouble();
        double cumulativeProbability = 0.0;
        for (int i = 0; i < probabilities.length; i++) {
            cumulativeProbability += probabilities[i];
            if (randomValue <= cumulativeProbability) {
                return values[i];
            }
        }
        return values[values.length - 1];
    }

    public String[][] getIATTable() {
        ArrayList<String[]> iatTable = new ArrayList<>();
        
        for (int i = 0; i < customers.size(); i++) {
            Customer customer = customers.get(i);
            String[] row = new String[3];
            row[0] = "C" + i;
            row[1] = String.valueOf(customer.IAT);
            row[2] = String.valueOf(customer.ArrivalTime);
            iatTable.add(row);
        }
        
        return iatTable.toArray(new String[0][3]);
    }

    public static int getIntInput(String message) {
        String input = JOptionPane.showInputDialog(message);
        return Integer.parseInt(input);
    }

    public static int[] getIntArrayInput(String message, int length) {
        int[] array = new int[length];
        for (int i = 0; i < length; i++) {
            array[i] = getIntInput(message + " (value " + (i + 1) + "):");
        }
        return array;
    }
        public void displayResults() {
        JFrame frame = new JFrame("Event Simulation Results");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        String[] iatColumnNames = {"Customer", "IAT", "Arrival Time"};
        String[][] iatData = getIATTable();
        JTable iatTable = new JTable(iatData, iatColumnNames);
        JScrollPane iatScrollPane = new JScrollPane(iatTable);

        String[] eventColumnNames = {"Type", "Clock", "LQ", "LS", "Checkout Line"};
        String[][] eventData = getSimulationTable();
        JTable eventTable = new JTable(eventData, eventColumnNames);
        JScrollPane eventScrollPane = new JScrollPane(eventTable);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, iatScrollPane, eventScrollPane);
        splitPane.setDividerLocation(300);

        frame.add(splitPane);
        frame.setVisible(true);
    }

    public String[][] getSimulationTable() {
        ArrayList<String[]> simulationTable = new ArrayList<>();
        ArrayList<String> checkoutLine = new ArrayList<>();
        
        for (Event event : eventList) {
            if (event.type.equals("A")) {
                checkoutLine.add("C" + event.customerId);
            } else if (event.type.equals("D")) {
                checkoutLine.remove("C" + event.customerId);
            }
            
            int lq = (int) checkoutLine.stream().filter(c -> !c.equals("C" + event.customerId)).count();
            int ls = checkoutLine.size() > 0 ? 1 : 0;
            
            String[] row = new String[5];
            row[0] = event.type;
            row[1] = String.valueOf(event.time);
            row[2] = String.valueOf(lq);
            row[3] = String.valueOf(ls);
            row[4] = String.join(" ", checkoutLine);
            simulationTable.add(row);
        }
        
        return simulationTable.toArray(new String[0][5]);
    }

    public static double[] generateProbabilities(String context, int length) {
        double[] probabilities = new double[length];
        String choice = JOptionPane.showInputDialog("Choose the type of probabilities for " + context + " (1 - Random, 2 - Same, 3 - Different):");
        switch (choice) {
            case "1":
                double sum = 0;
                for (int i = 0; i < length; i++) {
                    probabilities[i] = Math.random();
                    sum += probabilities[i];
                }
                for (int i = 0; i < length; i++) {
                    probabilities[i] /= sum;
                }
                break;
            case "2":
                for (int i = 0; i < length; i++) {
                    probabilities[i] = 1.0 / length;
                }
                break;
            case "3":
                for (int i = 0; i < length; i++) {
                    probabilities[i] = Double.parseDouble(JOptionPane.showInputDialog("Enter probability for " + context + " value " + (i + 1) + ":"));
                }
                break;
            default:
                for (int i = 0; i < length; i++) {
                    probabilities[i] = 1.0 / length;
                }
        }
        return probabilities;
    }

    public static void main(String[] args) {
        EventSimulation simulation = new EventSimulation();
        simulation.initializeSimulation();
        simulation.simulate();
        simulation.displayResults();
    }
}
