package com.simulator;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class Main {
    //Values of the congruential calculus (a * x(i) + c) % m
    private static final int a = 25173;
    private static final int c = 13849;
    private static final int m = 32768;
    private static final int count = 100000; //Amount of random generated numbers
    private static double x; //seed
    private static int randomCount;


    public static void main(String[] args) throws IOException {
        double minArrival = 1;
        double maxArrival = 2;

        double minService = 3;
        double maxService = 6;

        double[] seeds = {2.0, 2.1, 2.3, 2.2, 2.4}; // Seed number used in the randomizer
        //x = m / seeds[0];


        int servers = 2; // How many "cashiers"
        int capacity = 5; // Max possible amount in row, -1 is "infinite"
        int row = 0; // How full the row is

        double nextArrival = 3.0;
        double nextExit = -1;

        double globalTime = 0; // Time used for calculations
        double lastTime = 0;
        int losses = 0;

        double[] time;
        if (capacity == -1) {
            time = new double[10000];
        } else {
            time = new double[capacity + 1];
        }

        ArrayList<double[]> events = new ArrayList<>();  // 0 is Arrival and 1 is Exit, 1 is for time
        events.add(new double[]{0, 3.0});  // Reading is .get(listPosition)[arrayPosition]


        System.out.println("B");
        x = m / seeds[0];
        while (randomCount != count) {

            int lowestIndex = 0;
            for (int j = 0; j < events.size(); j++) {
                if (events.get(lowestIndex)[1] > events.get(j)[1]) {
                    lowestIndex = j;
                }
            }

            if (events.get(lowestIndex)[0] == 0) { // Arrival
                //System.out.println("Row: " + row + " Capacity: " + capacity);
                time[row] += events.get(lowestIndex)[1] - globalTime;
                if (row != capacity || capacity == -1) {

                    //System.out.println(row);
                    row++;

                    if (servers >= row) {
                        //System.out.println("ATENDIDO");

                        nextExit = conversion(minService, maxService, randomizer());


                        double v = nextExit + globalTime;
                        events.add(new double[]{1, v});
                    }

                } else {
                    losses++;
                }
                //System.out.println("ARRIVAL");

                nextArrival = conversion(minArrival, maxArrival, randomizer());
                double v = nextArrival + globalTime;
                globalTime = events.get(lowestIndex)[1];
                events.add(new double[]{0, nextArrival + globalTime});
                events.remove(lowestIndex);

                //System.out.println(globalTime);

            } else {

                //System.out.println("Row: " + row + " Capacity: " + capacity);
                //System.out.println("EXIT");

                time[row] += events.get(lowestIndex)[1] - globalTime;
                row--;
                double v = nextExit + globalTime;
                globalTime = events.get(lowestIndex)[1];
                events.remove(lowestIndex);

                if (row >= servers) {
                    events.add(new double[]{1, v});
                }
            }
        }
        System.out.println("C");

        int iterator = 0;
        double totalTime = 0;

        for (double t : time) {
            System.out.println(iterator + " " + t);
            iterator++;
            totalTime += t;
        }

        System.out.println("Losses: " + losses);
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
