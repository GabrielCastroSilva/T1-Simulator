package com.simulator;

import java.util.Arrays;
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


    public static Row[] initializer(){
         /*
        String id, // ID only used for print, do *NOT* mistake it for the destination in routing
        int servers,
        int capacity,  // if -1 then its maximum integer limit (no capacity limit)
        double minService,
        double maxService
        double[][] routing {probability, position in array of destination}
         */
        Row r0 = new Row(0, 1, -1, 1, 1.5, new double[][]{{0.8, 1}, {0.2, 2}});  // Only first row, will receive the arrivals unless changed in the first event
        Row r1 = new Row(1, 3, 5, 5, 10, new double[][]{{0.3, 0}, {0.5, 2}} );
        Row r2 = new Row(2, 2, 8, 10, 20, new double[][]{{0.7, 1}});

        return new Row[]{r0, r1, r2};

    }


    public static void main(String[] args) { // G/G/2/3   Geometric distribution of arrival and attendance with 2 server 3 capacity
        double minArrival = 1.0;
        double maxArrival = 4.0;


        Row[] rows = initializer(); // Init for row array

        double[] seeds = {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0}; // Seed number used in the randomizer

        int loops = 5; // Number of times the simulation will be ran
        double nextArrival, nextExit; // Aux variables used in calculations
        double globalTime = 0; // Time used for calculations


        Comparator<double[]> comparator = (event1, event2) -> { // Comparator for event times(earliest/lowest takes priority)
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

        for (int i = 0; i < loops; i++) {
            events.add(new double[]{0, 1, 0});  //First arrival //Reading is .get(listPosition)[arrayPosition]
            randomCount = 0;
            x = seeds[i];
            while (randomCount <= count) {

                double[] lowest = events.poll(); // Extracts and removes event with lowest time
                assert lowest != null; // In case it breaks

                if (lowest[0] == 0) { // Arrival

                    Row row;

                    for (Row r : rows) { // Time contabilization
                        r.setTime(lowest[1] - globalTime);
                    }
                    int position = (int) lowest[2];

                    globalTime = lowest[1];

                    if (rows[position].getRowSize() < rows[position].getCapacity()) {

                        rows[position].setRowSize(+1);

                        if (rows[position].getRowSize() <= rows[position].getServers()) {

                            nextExit = conversion(rows[position].getMinService(), rows[position].getMaxService(), randomizer());


                            double v = nextExit + globalTime;

                            int result = rows[position].getRoutings().length > 0 ? (int) rows[position].getRoutings()[0][1] : -1;

                            if(rows[position].getRoutings().length > 0 && rows[position].getRoutings()[0][0] < 1.0 ){
                                double rand = conversion(0, 1 , randomizer());

                                result = rows[position].setExit(rand);
                            }

                            if (result >= 0) {
                                events.add(new double[]{2, v, position, result});
                            } else {
                                events.add(new double[]{1, v, position});
                            }



                        }

                    } else {
                        rows[position].setLoss();
                    }

                    nextArrival = conversion(minArrival, maxArrival, randomizer());

                    double v = nextArrival + globalTime;
                    events.add(new double[]{0, v, position});


                } else if (lowest[0] == 1) { // Exit
                    int position = (int) lowest[2];
                    for (Row r : rows) {
                        r.setTime(lowest[1] - globalTime);
                    }

                    globalTime = lowest[1];
                    rows[position].setRowSize(-1);

                    nextExit = conversion(rows[position].getMinService(), rows[position].getMaxService(), randomizer());

                    double v = nextExit + globalTime;


                    if (rows[position].getRowSize() >= rows[position].getServers()) {
                        int result = rows[position].getRoutings().length > 0 ? (int) rows[position].getRoutings()[0][1] : -1;

                        if(rows[position].getRoutings().length > 0 && rows[position].getRoutings()[0][0] < 1.0 ){
                            double rand = conversion(0, 1 , randomizer());

                            result = rows[position].setExit(rand);
                        }

                        if (result >= 0) {
                            events.add(new double[]{2, v, position, result});
                        } else {
                            events.add(new double[]{1, v, position});
                        }

                    }

                } else if (lowest[0] == 2) { // Passage
                    int position = (int) lowest[2];
                    int secondPosition = (int) lowest[3];

                    for (Row r : rows) {
                        r.setTime(lowest[1] - globalTime);
                    }

                    globalTime = lowest[1];
                    rows[position].setRowSize(-1);


                    if (rows[position].getRowSize() >= rows[position].getServers()) {
                        nextExit = conversion(rows[position].getMinService(), rows[position].getMaxService(), randomizer());

                        double v = nextExit + globalTime;
                        int result = rows[position].getRoutings().length > 0 ? (int) rows[position].getRoutings()[0][1] : -1;

                        if(rows[position].getRoutings().length > 0 && rows[position].getRoutings()[0][0] < 1.0 ){
                            double rand = conversion(0, 1 , randomizer());

                            result = rows[position].setExit(rand);
                        }

                        if (result >= 0) {
                            events.add(new double[]{2, v, position, result});
                        } else {
                            events.add(new double[]{1, v, position});
                        }
                    }

                    if (rows[secondPosition].getRowSize() < rows[secondPosition].getCapacity()) {
                        rows[secondPosition].setRowSize(+1);
                        if (rows[secondPosition].getRowSize() <= rows[secondPosition].getServers()) {
                            nextExit = conversion(rows[secondPosition].getMinService(), rows[secondPosition].getMaxService(), randomizer());

                            double v = nextExit + globalTime;
                            int result = rows[secondPosition].getRoutings().length > 0 ? (int) rows[secondPosition].getRoutings()[0][1] : -1;

                            if(rows[secondPosition].getRoutings().length > 0 && rows[secondPosition].getRoutings()[0][0] < 1.0 ){
                                double rand = conversion(0, 1 , randomizer());

                                result = rows[secondPosition].setExit(rand);
                            }

                            if (result >= 0) {
                                events.add(new double[]{2, v, secondPosition, result});
                            } else {
                                events.add(new double[]{1, v, secondPosition});
                            }
                        }
                    } else {
                        rows[secondPosition].setLoss();
                    }
                }
            }
            events = new PriorityQueue<>(comparator);
            for (Row r : rows) {
                r.resetRow();
            }
            globalTime = 0;
        }

        for (Row r : rows) {
            r.getResults(loops);
        }

    }

    //static double[] r = {0.9921, 0.0004, 0.5534, 0.2761, 0.3398, 0.8963, 0.9023, 0.0132, 0.4569, 0.5121, 0.9208, 0.0171, 0.2299, 0.8545, 0.6001, 0.2921};

    // Method that executes the linear congruential calculation
    public static double randomizer() {
        randomCount++;
        x = (a * x + c) % m;

        return x/m;
    }

    public static double conversion(double A, double B, double random) {
        return (B - A) * (random) + A;
    }

}
