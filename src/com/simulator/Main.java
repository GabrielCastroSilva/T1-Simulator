package com.simulator;

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
        double minArrival = 1.0;
        double maxArrival = 4.0;

        double[] minService = {2, 3};
        double[] maxService = {5, 5};

        /*
        String id,
        int servers,
        int capacity,
        double minArrival,
        double maxArrival,
        double minService,
        double maxService
         */

        Row r0 = new Row(0, 1, -1, 1, 1.5, new double[][]{{0.8, 1}, {0.2, 2}});  // First row, will receive the arrivals unless changed in the first event
        Row r1 = new Row(1, 3, 5, 5, 10, new double[][]{{0.3, 0}, {0.5, 2}});
        Row r2 = new Row(2, 2, 8, 10, 20, new double[][]{{0.7, 1}});

        Row[] rows = {r0, r1, r2};

        double[] seeds = {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0}; // Seed number used in the randomizer

        int loops = 1;

        double nextArrival = 3.0;
        double nextExit = -1;

        double globalTime = 0; // Time used for calculations


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
        events.add(new double[]{0, 1, 0});  // Reading is .get(listPosition)[arrayPosition]

        for (int i = 0; i < loops; i++) {

            x = seeds[i];
            while (randomCount <= count) {

                double[] lowest = events.poll();

                assert lowest != null;
                if (lowest[0] == 0) { // Arrival

                    for (Row r : rows) {
                        r.setTime(lowest[1] - globalTime);
                    }
                    int position = (int) lowest[2];

                    globalTime = lowest[1];

                    if (rows[position].getRowSize() < rows[position].getCapacity()) {

                        rows[position].setRowSize(+1);

                        if (rows[position].getRowSize() <= rows[position].getServers()) {

                            nextExit = conversion(rows[position].getMinService(), rows[position].getMaxService(), randomizer());

                            double v = nextExit + globalTime;

                            double rand = conversion(0, 1, randomizer());
                            int result = rows[position].setExit(rand);
                            if (result > 0) {
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

                    nextExit = conversion(minService[1], maxService[1], randomizer());
                    double v = nextExit + globalTime;

                    if (rows[position].getRowSize() >= rows[position].getServers()) {
                        double rand = conversion(0, 1, randomizer());
                        int result = rows[position].setExit(rand);
                        if (result > 0) {
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
                    rows[secondPosition].setRowSize(+1);


                    if (rows[position].getRowSize() >= rows[position].getServers()) {
                        nextExit = conversion(rows[position].getMinService(), rows[position].getMaxService(), randomizer());
                        double v = nextExit + globalTime;
                        double rand = conversion(0, 1, randomizer());
                        int result = rows[position].setExit(rand);
                        if (result > 0) {
                            events.add(new double[]{2, v, position, result});
                        } else {
                            events.add(new double[]{1, v, position});
                        }
                    }

                    if (rows[secondPosition].getRowSize() < rows[secondPosition].getCapacity()) {
                        if (rows[secondPosition].getRowSize() <= rows[secondPosition].getServers()) {
                            nextExit = conversion(rows[secondPosition].getMinService(), rows[secondPosition].getMaxService(), randomizer());
                            double v = nextExit + globalTime;
                            events.add(new double[]{1, v, secondPosition});
                        }
                    } else {
                        rows[secondPosition].setLoss();
                    }
                }
            }

            randomCount = 0;
            events = new PriorityQueue<>(comparator);
            events.add(new double[]{0, 1, 0});
            //row[0] = 0;
            //row[1] = 0;
            globalTime = 0;
        }


        for (Row r : rows) {
            r.getResults();
        }

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
