
package pkgfinal.modeling;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NewsVendorSimulation {
    public static void run() {
        try {
            double costToBuy = getDoubleInput("Enter cost to buy a Part (in cents):");
            double costToSell = getDoubleInput("Enter cost to sell a part (in cents):");
            double lostProfit = costToSell - costToBuy;
            double scrapValue = getDoubleInput("Enter scrap value per unsold part (in cents):");
            int days = getIntInput("Enter number of days to simulate:");
            int purchasedPapers = getIntInput("Enter the number of parts to be purchased for all days:");

            String newsTypeChoice = getStringInput("Enter probability choice for partType (same, different, random):");
            List<NewsType> newsTypes = NewsType.generateNewsTypeTable(newsTypeChoice);

            displayTable("PartsType Table", newsTypes);

            int numberOfDemands = getIntInput("Enter the number of demands:");
            int minDemandValue = getIntInput("Enter the minimum demand value:");
            String demandChoice = getStringInput("Enter probability choice for Demand (same, different, random):");
            List<Demand> demands = Demand.generateDemandTable(demandChoice, numberOfDemands, minDemandValue);

            displayTable("Demand Table", demands);

            Simulation.runSimulation(costToBuy, costToSell, lostProfit, scrapValue, days, purchasedPapers, newsTypes, demands);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: Invalid input. Please try again.");
        }
    }

    public static void displayTable(String title, List<?> objects) {
        String[][] data = new String[objects.size()][4];
        String[] columns = {"Type", "Probability", "Cumulative", "Range"};

        for (int i = 0; i < objects.size(); i++) {
            if (objects.get(i) instanceof NewsType) {
                NewsType nt = (NewsType) objects.get(i);
                data[i] = new String[]{nt.getType(), String.valueOf(nt.getProbability()), String.valueOf(nt.getCumulative()), nt.getRange()};
            } else if (objects.get(i) instanceof Demand) {
                Demand d = (Demand) objects.get(i);
                data[i] = new String[]{d.demandType, String.valueOf(d.probabilityGood), String.valueOf(d.cumulativeGood), d.rangeGood};
            }
        }

        JTable table = new JTable(data, columns);
        JScrollPane scrollPane = new JScrollPane(table);
        JFrame frame = new JFrame(title);
        frame.add(scrollPane);
        frame.setSize(800, 400);
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
                JOptionPane.showMessageDialog(null, "Invalid input. Please enter a valid number.");
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
    
public static class NewsType {
    final String type;
    double probability;
    double cumulative;
    String range;

    public NewsType(String type) {
        this.type = type;
    }

    public void setProbability(double probability) {
        this.probability = probability;
    }

    public void setCumulative(double cumulative) {
        this.cumulative = cumulative;
    }

    public void setRange(String range) {
        this.range = range;
    }

    public String getType() {
        return type;
    }

    public double getProbability() {
        return probability;
    }

    public double getCumulative() {
        return cumulative;
    }

    public String getRange() {
        return range;
    }

    public static List<NewsType> generateNewsTypeTable(String choice) {
        List<NewsType> newsTypes = new ArrayList<>();
        newsTypes.add(new NewsType("Good"));
        newsTypes.add(new NewsType("Fair"));
        newsTypes.add(new NewsType("Poor"));

        double totalProb = 0.0;
        
        switch (choice.toLowerCase()) {
            case "same":
                double equalProb = 1.0 / newsTypes.size();
                for (NewsType nt : newsTypes) {
                    nt.setProbability(equalProb);
                }
                break;

            case "random":
                Random random = new Random();
                double remainingProb = 1.0;
                for (int i = 0; i < newsTypes.size(); i++) {
                    double prob = (i == newsTypes.size() - 1) ? remainingProb : random.nextDouble() * remainingProb;
                    remainingProb -= prob;
                    newsTypes.get(i).setProbability(prob);
                }
                break;

            case "different":
                double cumulativeSum = 0.0;
                for (NewsType nt : newsTypes) {
                    while (true) {
                        try {
                            String input = JOptionPane.showInputDialog("Enter probability for " + nt.getType() + " (0 to 1):");
                            if (input == null) throw new IllegalArgumentException("User cancelled the input.");
                            double prob = Double.parseDouble(input);
                            if (prob < 0 || prob > 1) {
                                JOptionPane.showMessageDialog(null, "Invalid probability. Please enter a number between 0 and 1.");
                                continue;
                            }
                            cumulativeSum += prob;
                            if (cumulativeSum > 1.0) {
                                JOptionPane.showMessageDialog(null, "Error: Cumulative probability exceeds 1.0. Please re-enter all probabilities.");
                                return generateNewsTypeTable(choice);
                            }
                            nt.setProbability(prob);
                            break;
                        } catch (Exception e) {
                            JOptionPane.showMessageDialog(null, "Invalid input. Please enter a valid number.");
                        }
                    }
                }

                if (cumulativeSum < 1.0) {
                    JOptionPane.showMessageDialog(null, "Error: Probabilities do not sum to 1.0. Please re-enter all probabilities.");
                    return generateNewsTypeTable(choice);
                }
                break;

            default:
                JOptionPane.showMessageDialog(null, "Invalid choice! Defaulting to equal probabilities.");
                double defaultProb = 1.0 / newsTypes.size();
                for (NewsType nt : newsTypes) {
                    nt.setProbability(defaultProb);
                }
        }

        double cumulative = 0.0;
        int lowerBound = 1;
        for (NewsType nt : newsTypes) {
            cumulative += nt.getProbability() * 100;
            int upperBound = (int) Math.round(cumulative);
            nt.setCumulative(cumulative);
            nt.setRange(lowerBound + "-" + upperBound);
            lowerBound = upperBound + 1;
        }

        if (lowerBound <= 100) {
            int lastIndex = newsTypes.size() - 1;
            NewsType lastNewsType = newsTypes.get(lastIndex);
            lastNewsType.setRange(lastNewsType.getRange().split("-")[0] + "-100");
        }

        return newsTypes;
    }
}
    public static class Demand {
    final String demandType;
    double probabilityGood;
    double probabilityFair;
    double probabilityPoor;
    double cumulativeGood;
    double cumulativeFair;
    double cumulativePoor;
    String rangeGood;
    String rangeFair;
    String rangePoor;

    public Demand(String demandType) {
        this.demandType = demandType;
    }

    public void setProbabilities(double good, double fair, double poor) {
        this.probabilityGood = good;
        this.probabilityFair = fair;
        this.probabilityPoor = poor;
    }

    public void setCumulatives(double good, double fair, double poor) {
        this.cumulativeGood = good;
        this.cumulativeFair = fair;
        this.cumulativePoor = poor;
    }

    public void setRanges(String good, String fair, String poor) {
        this.rangeGood = good;
        this.rangeFair = fair;
        this.rangePoor = poor;
    }

    public static List<Demand> generateDemandTable(String choice, int numberOfDemands, int minDemandValue) {
        List<Demand> demands = new ArrayList<>();
        for (int i = 0; i < numberOfDemands; i++) {
            demands.add(new Demand("Demand " + (minDemandValue + i * 10))); // The demand values increment by 10
        }

        double[] randomProbabilitiesGood = new double[numberOfDemands];
        double[] randomProbabilitiesFair = new double[numberOfDemands];
        double[] randomProbabilitiesPoor = new double[numberOfDemands];

        if (choice.equalsIgnoreCase("random")) {
            Random random = new Random();
            double sumOfRandomsGood = 0.0, sumOfRandomsFair = 0.0, sumOfRandomsPoor = 0.0;
            
            for (int i = 0; i < numberOfDemands; i++) {
                randomProbabilitiesGood[i] = random.nextDouble();
                randomProbabilitiesFair[i] = random.nextDouble();
                randomProbabilitiesPoor[i] = random.nextDouble();
                
                sumOfRandomsGood += randomProbabilitiesGood[i];
                sumOfRandomsFair += randomProbabilitiesFair[i];
                sumOfRandomsPoor += randomProbabilitiesPoor[i];
            }

            for (int i = 0; i < numberOfDemands; i++) {
                randomProbabilitiesGood[i] /= sumOfRandomsGood; 
                randomProbabilitiesFair[i] /= sumOfRandomsFair; 
                randomProbabilitiesPoor[i] /= sumOfRandomsPoor; 
            }
        }

        double totalGoodProb = 0.0;
        double totalFairProb = 0.0;
        double totalPoorProb = 0.0;

        for (int i = 0; i < demands.size(); i++) {
            Demand d = demands.get(i);
            
            switch (choice.toLowerCase()) {
                case "same":
                    d.setProbabilities(1.0 / numberOfDemands, 1.0 / numberOfDemands, 1.0 / numberOfDemands);
                    break;

                case "different":
                    double good = getDoubleInput("Enter probability for " + d.demandType + " (Good):");
                    double fair = getDoubleInput("Enter probability for " + d.demandType + " (Fair):");
                    double poor = getDoubleInput("Enter probability for " + d.demandType + " (Poor):");

                    d.setProbabilities(good, fair, poor);
                    break;

                case "random":
                    double goodRandom = randomProbabilitiesGood[i];
                    double fairRandom = randomProbabilitiesFair[i];
                    double poorRandom = randomProbabilitiesPoor[i];
                    
                    d.setProbabilities(goodRandom, fairRandom, poorRandom);
                    break;

                default:
                    JOptionPane.showMessageDialog(null, "Invalid choice! Defaulting to equal probabilities.");
                    d.setProbabilities(1.0 / numberOfDemands, 1.0 / numberOfDemands, 1.0 / numberOfDemands);
            }

            totalGoodProb += d.probabilityGood;
            totalFairProb += d.probabilityFair;
            totalPoorProb += d.probabilityPoor;
        }

        if (totalGoodProb > 1.0 || totalFairProb > 1.0 || totalPoorProb > 1.0) {
            JOptionPane.showMessageDialog(null, "Error: The total probabilities for one or more categories exceed 1.0.");
            return generateDemandTable(choice, numberOfDemands, minDemandValue);
        }

        double cumulativeGood = 0.0, cumulativeFair = 0.0, cumulativePoor = 0.0;
        int lowerBoundGood = 1, lowerBoundFair = 1, lowerBoundPoor = 1;

        for (Demand d : demands) {
            cumulativeGood += d.probabilityGood * 100;
            cumulativeFair += d.probabilityFair * 100;
            cumulativePoor += d.probabilityPoor * 100;

            int upperGood = (int) Math.round(cumulativeGood);
            int upperFair = (int) Math.round(cumulativeFair);
            int upperPoor = (int) Math.round(cumulativePoor);

            d.setCumulatives(cumulativeGood, cumulativeFair, cumulativePoor);
            d.setRanges(
                lowerBoundGood + "-" + upperGood,
                lowerBoundFair + "-" + upperFair,
                lowerBoundPoor + "-" + upperPoor
            );

            lowerBoundGood = upperGood + 1;
            lowerBoundFair = upperFair + 1;
            lowerBoundPoor = upperPoor + 1;
        }

        return demands;
    }

    public static double getDoubleInput(String message) {
        while (true) {
            try {
                String input = JOptionPane.showInputDialog(message);
                if (input == null) throw new IllegalArgumentException("User cancelled the input.");
                return Double.parseDouble(input);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Invalid input. Please enter a valid number.");
            }
        }
    }
}

    public static class Simulation {

        public static void runSimulation(double costToBuy, double costToSell, double lostProfit, double scrapValue, int days, 
        int purchasedPapers, List<NewsType> newsTypes, List<Demand> demands) {

            Random random = new Random();
            List<String[]> results = new ArrayList<>();

            double totalRevenue = 0.0;
            double totalCost = 0.0;
            double totalLostProfit = 0.0;
            double totalSalvage = 0.0;
            double totalProfit = 0.0;

            for (int day = 1; day <= days; day++) {
                int newsTypeRand = random.nextInt(100) + 1;
                int demandRand = random.nextInt(100) + 1;

                NewsType selectedNewsType = null;
                for (NewsType nt : newsTypes) {
                    if (isWithinRange(newsTypeRand, nt.getRange())) {
                        selectedNewsType = nt;
                        break;
                    }
                }

                Demand selectedDemand = null;
                for (Demand d : demands) {
                    if (isWithinRange(demandRand, d.rangeGood)) { 
                        selectedDemand = d;
                        break;
                    }
                }

                int demand = selectedDemand != null ? Integer.parseInt(selectedDemand.demandType.split(" ")[1]) : 0;

                double revenue = demand * costToSell; 
                double cost = purchasedPapers * costToBuy; 
                double lost = demand > purchasedPapers ? (demand - purchasedPapers) * lostProfit : 0.0; 
                double salvage = demand < purchasedPapers ? (purchasedPapers - demand) * scrapValue : 0.0; 
                double profit = revenue - cost - lost + salvage; 

                totalRevenue += revenue;
                totalCost += cost;
                totalLostProfit += lost;
                totalSalvage += salvage;
                totalProfit += profit;

                results.add(new String[] {
                    String.valueOf(day), 
                    String.valueOf(purchasedPapers), 
                    String.valueOf(newsTypeRand), 
                    selectedNewsType != null ? selectedNewsType.getType() : "N/A",
                    String.valueOf(demandRand), 
                    String.valueOf(demand), 
                    String.format("%.2f", revenue), 
                    String.format("%.2f", lost), 
                    String.format("%.2f", salvage), 
                    String.format("%.2f", profit)
                });
            }

            displaySimulationTable(results);

            String summaryMessage = String.format(
                "Summary of Simulation:\n" +
                "Total Revenue: $%.2f\n" +
                "Total Cost: $%.2f\n" +
                "Total Lost Profit: $%.2f\n" +
                "Total Salvage: $%.2f\n" +
                "Total Profit: $%.2f", 
                totalRevenue, totalCost, totalLostProfit, totalSalvage, totalProfit
            );

            JOptionPane.showMessageDialog(null, summaryMessage, "Simulation Summary", JOptionPane.INFORMATION_MESSAGE);
        }

        private static boolean isWithinRange(int value, String range) {
            String[] bounds = range.split("-");
            int lower = Integer.parseInt(bounds[0]);
            int upper = Integer.parseInt(bounds[1]);
            return value >= lower && value <= upper;
        }

        public static void displaySimulationTable(List<String[]> results) {
            String[] columns = {"Day", "Purchased Part", "Random PartType", "PartType", "Random Demand", "Demand", "Revenue ($)", "Lost Profit ($)", "Salvage ($)", "Profit ($)"};
            JTable table = new JTable(results.toArray(new String[0][0]), columns);
            JScrollPane scrollPane = new JScrollPane(table);
            JFrame frame = new JFrame("Simulation Results");
            frame.add(scrollPane);
            frame.setSize(1000, 500);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setVisible(true);
        }
    }
}
