package com.simulator;

import java.util.ArrayList;

public class Main {
    //Values of the congruential calculus (a * x(i) + c) % m
    private static final int a = 25173;
    private static final int c = 13849;
    private static final int m = 137921;
    private static final int count = 100000; //Amount of random generated numbers
    private static double x; //seed
    private static int randomCount;


    public static void main(String[] args) {
        double minArrival = 2;
        double maxArrival = 4;

        double minService = 3;
        double maxService = 5;

        double[] seeds = {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0}; // Seed number used in the randomizer


        int[] servers = {2}; // How many "cashiers"
        int[] capacity = {5}; // Max possible amount in row, -1 is "infinite"
        int row = 0; // How full the row is

        int loops = 5;

        double nextArrival = 3.0;
        double nextExit = -1;

        double globalTime = 0; // Time used for calculations
        int losses = 0;

        double[] time = new double[capacity[0] + 1];

        ArrayList<double[]> events = new ArrayList<>();  // 0 is Arrival and 1 is Exit, 1 is for time
        events.add(new double[]{0, 3.0});  // Reading is .get(listPosition)[arrayPosition]
        double totalTime = 0;

        for (int i = 0; i < loops; i++) {

            x = seeds[i];
            while (randomCount <= count) {

                int lowestIndex = 0;
                for (int j = 0; j < events.size(); j++) {
                    if (events.get(lowestIndex)[1] > events.get(j)[1]) {
                        lowestIndex = j;
                    }
                }

                if (events.get(lowestIndex)[0] == 0) { // Arrival
                    time[row] += events.get(lowestIndex)[1] - globalTime;
                    globalTime = events.get(lowestIndex)[1];
                    if (row != capacity[0]) {

                        row++;

                        if (servers[0] >= row) {

                            nextExit = conversion(minService, maxService, randomizer());


                            double v = nextExit + globalTime;
                            events.add(new double[]{1, v});
                        }

                    } else {
                        losses++;
                    }

                    nextArrival = conversion(minArrival, maxArrival, randomizer());
                    double v = nextArrival + globalTime;
                    events.add(new double[]{0, nextArrival + globalTime});
                    events.remove(lowestIndex);


                } else if(events.get(lowestIndex)[0] == 1) { // Exit
                    time[row] += events.get(lowestIndex)[1] - globalTime;
                    globalTime = events.get(lowestIndex)[1];
                    row--;
                    nextExit = conversion(minService, maxService, randomizer());
                    double v = nextExit + globalTime;
                    events.remove(lowestIndex);

                    if (row >= servers[0]) {
                        events.add(new double[]{1, v});
                    }
                }
            }

            randomCount = 0;
            events = new ArrayList<>();
            events.add(new double[]{0, 3.0});
            row = 0;
            globalTime = 0;

        }

        int iterator = 0;

        for(int t = 0 ; t < time.length ; t++){
            totalTime += time[t];
        }

        totalTime /= loops;


        System.out.println("State       Time       Probability");

        for(int t2 = 0; t2 < time.length ; t2++){
            double percent = (100 * time[t2]) / totalTime;
            System.out.printf("%d\t%.4f\t%.2f%%\n", iterator, (time[t2]/loops), ((time[t2] * 100) / totalTime)/loops);
        }



        System.out.println("Losses: " + losses/loops);
        System.out.println("Total Time: " + totalTime);

        /*for (int j = 0; j < count; j++) {
            // Writes to a .txt file the results of the randomizer method
            System.out.println(randomizer());

        }*/
    }

    // Method that executes the linear congruential calculation
    public static double randomizer() {
        randomCount++;
        x = (a * x + c) % m;

        return x / m;
    }

    public static double conversion(double A, double B, double random) {
        return (B - A) * (random) + A;
    }

}
