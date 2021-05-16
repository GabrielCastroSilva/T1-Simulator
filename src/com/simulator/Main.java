package com.simulator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

public class Main {
    //Values of the congruential calculus (a * x(i) + c) % m
    private static final int a = 25173;
    private static final int c = 13849;
    private static final int m = 137921;
    private static final int count = 100000; //Amount of random generated numbers
    private static double x; //seed
    private static int randomCount;


    public static void main(String[] args) { // G/G/2/3   Distribuicao geometrica na chegada e atendimento com 2 server 3 capacity
        double[] minArrival = {2};
        double[] maxArrival = {3};

        double[] minService = {2, 3};
        double[] maxService = {5, 5};

        //String id, int servers, int capacity, double minArrival, double maxArrival, double minService, double maxService
        Row r0 = new Row("Row0", 2, 3,2,5);
        Row r1 = new Row("Row1",1,3,3,5);

        double[] seeds = {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0}; // Seed number used in the randomizer


        int[] servers = {2, 1}; // How many "cashiers"
        int[] capacity = {3, 3}; // Max possible amount in row, -1 is "infinite"
        int[] row = {0, 0}; // How full the row is

        int loops = 5;

        double nextArrival = 3.0;
        double nextExit = -1;

        double globalTime = 0; // Time used for calculations


        int numberOfRows = 2;
        int[] losses = new int[numberOfRows];
        double[][] time = new double[numberOfRows][capacity[0] + 1];


        double totalTime = 0;

        Comparator<double[]> comparator = (event1, event2) -> { // Comparator for event times(lowest takes priority)
            double aux = event1[1] - event2[1];
            if (aux < 0) {
                return -1;
            }
            if (aux > 0) {
                return 1;
            }
            return 0;
        };

        PriorityQueue<double[]> events = new PriorityQueue<>(comparator); // 0 is Arrival and 1 is Exit, 2 is for passage
        events.add(new double[]{0, 2.5});  // Reading is .get(listPosition)[arrayPosition]

        for (int i = 0; i < loops; i++) {

            x = seeds[i];
            while (randomCount <= count) {

                double[] lowest = events.poll();


                assert lowest != null;
                if (lowest[0] == 0) { // Arrival
                    time[0][row[0]] += lowest[1] - globalTime;
                    time[1][row[1]] += lowest[1] - globalTime;
                    globalTime = lowest[1];
                    if (row[0] < capacity[0]) {

                        row[0]++;

                        if (row[0] <= servers[0]) {

                            nextExit = conversion(minService[0], maxService[0], randomizer());

                            double v = nextExit + globalTime;
                            events.add(new double[]{2, v});
                        }

                    } else {
                        losses[0]++;
                    }

                    nextArrival = conversion(minArrival[0], maxArrival[0], randomizer());
                    double v = nextArrival + globalTime;
                    events.add(new double[]{0, v});


                } else if (lowest[0] == 1) { // Exit
                    time[0][row[0]] += lowest[1] - globalTime;
                    time[1][row[1]] += lowest[1] - globalTime;
                    globalTime = lowest[1];
                    row[1]--;
                    nextExit = conversion(minService[1], maxService[1], randomizer());
                    double v = nextExit + globalTime;

                    if (row[1] >= servers[1]) {
                        events.add(new double[]{1, v});
                    }
                } else if (lowest[0] == 2) { // Passage
                    time[0][row[0]] += lowest[1] - globalTime;
                    time[1][row[1]] += lowest[1] - globalTime;
                    row[0]--;
                    globalTime = lowest[1];

                    if (row[0] >= servers[0]) {
                        nextExit = conversion(minService[0], maxService[0], randomizer());
                        double v = nextExit + globalTime;
                        events.add(new double[]{2, v});
                    }
                    if (row[1] < capacity[1]) {
                        row[1]++;
                        if (row[1] <= servers[1]) {
                            nextExit = conversion(minService[1], maxService[1], randomizer());
                            double v = nextExit + globalTime;
                            events.add(new double[]{1, v});
                        }
                    } else {
                        losses[1]++;
                    }


                }
            }

            randomCount = 0;
            events = new PriorityQueue<>(comparator);
            events.add(new double[]{0, 2.5});
            row[0] = 0;
            row[1] = 0;
            globalTime = 0;
        }

        int iterator = 0;
        for (int z = 0; z < numberOfRows; z++) {
            totalTime = 0;
            for (int t = 0; t < time[z].length; t++) {
                totalTime += time[z][t];
            }

            totalTime /= loops;


            System.out.println("State       Time       Probability");

            for (int t2 = 0; t2 < time[z].length; t2++) {
                double percent = (100 * time[z][t2]) / totalTime;
                System.out.printf("%d\t%.4f\t%.2f%%\n", t2, (time[z][t2] / loops), ((time[z][t2] * 100) / totalTime) / loops);
            }


            System.out.println("Losses: " + losses[z] / loops);
            System.out.println("Total Time: " + totalTime);
        }


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
