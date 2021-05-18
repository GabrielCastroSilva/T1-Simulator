package com.simulator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Main {
    //Values of the congruential calculus (a * x(i) + c) % m
    private static final int a = 25173;
    private static final int c = 13849;
    private static final int m = 137921;
    private static int count; //Amount of random generated numbers
    private static double x; //seed
    private static int randomCount;
    private static PriorityQueue<double[]> events;
    private static List<Integer> seeds;
    private static int loops;
    private static Map<Integer, Double> arrivalTimes;


    public static Row[] initializer() throws IOException {
         /*
        String id, // ID used for routing
        int servers,
        int capacity,  // if -1 then its maximum integer limit (no capacity limit)
        double minService,
        double maxService
        double[][] routing {probability, position in array of destination}
         */
        /*Row r0 = new Row(0, 1, -1, 1, 1.5, 1, 1.5, new double[][]{{0.8, 1}, {0.2, 2}});  // Only first row, will receive the arrivals unless changed in the first event
        Row r1 = new Row(1, 3, 5, 5, 10, 0, 0, new double[][]{{0.3, 0}, {0.5, 2}});
        Row r2 = new Row(2, 2, 8, 10, 20, 0, 0, new double[][]{{0.7, 1}});*/

        //return new Row[]{r0, r1, r2};

        ObjectMapper factory = new ObjectMapper(new YAMLFactory());
        YamlReader reader = factory.readValue(new File("model.yml"), YamlReader.class);

        seeds = reader.getSeeds();
        loops = reader.getLoops();
        count = reader.getRndnumbersPerSeed();
        arrivalTimes = reader.getArrivals();


        return reader.rowsInit();

    }


    public static void main(String[] args) throws IOException { // G/G/2/3   Geometric distribution of arrival and attendance with 2 server 3 capacity


        Row[] rows = initializer(); // Init for row array

        //double[] seeds = {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0}; // Seed number used in the randomizer

        //int loops = 5; // Number of times the simulation will be ran
        double nextArrival, nextExit, nextPassage; // Aux variables used in calculations
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

        events = new PriorityQueue<>(comparator); // 0 is Arrival and 1 is Exit, 2 is for passage

        for (int i = 0; i < loops; i++) {
            //events.add(new double[]{0, 1, 0});  //First arrival //Reading is .get(listPosition)[arrayPosition]
            for (Map.Entry<Integer, Double> entry : arrivalTimes.entrySet()) {
                int pos = entry.getKey();
                double time = entry.getValue();
                events.add(new double[]{0, time, pos});
            }
            randomCount = 0;
            x = seeds.get(i);
            while (randomCount <= count) {

                double[] lowest = events.poll(); // Extracts and removes event with lowest time
                assert lowest != null; // In case it breaks

                if (lowest[0] == 0) { // Arrival

                    Row row = null;

                    for (Row r : rows) { // Time contabilization
                        r.setTime(lowest[1] - globalTime);
                        if (r.getId() == (int) lowest[2]) {
                            row = r;
                        }
                    }

                    globalTime = lowest[1];

                    assert row != null;
                    if (row.getRowSize() < row.getCapacity()) {

                        row.setRowSize(+1);

                        if (row.getRowSize() <= row.getServers()) {

                            rowProbability(row, globalTime);


                        }

                    } else {
                        row.setLoss();
                    }

                    nextArrival = conversion(row.getMinArrival(), row.getMaxArrival(), randomizer());

                    double v = nextArrival + globalTime;
                    events.add(new double[]{0, v, row.getId()});


                } else if (lowest[0] == 1) { // Exit
                    Row row = null;
                    for (Row r : rows) {
                        r.setTime(lowest[1] - globalTime);
                        if (r.getId() == (int) lowest[2]) {
                            row = r;
                        }
                    }

                    assert row != null;
                    globalTime = lowest[1];
                    row.setRowSize(-1);

                    nextPassage = conversion(row.getMinService(), row.getMaxService(), randomizer());

                    double v = nextPassage + globalTime;


                    if (row.getRowSize() >= row.getServers()) {
                        rowProbability(row, globalTime);

                    }

                } else if (lowest[0] == 2) { // Passage
                    Row row = null;
                    Row secondRow = null;

                    for (Row r : rows) {
                        r.setTime(lowest[1] - globalTime);
                        if (r.getId() == (int) lowest[2]) {
                            row = r;
                        }
                        if (r.getId() == (int) lowest[3]) {
                            secondRow = r;
                        }
                    }

                    globalTime = lowest[1];
                    assert row != null;
                    row.setRowSize(-1);


                    if (row.getRowSize() >= row.getServers()) {
                        rowProbability(row, globalTime);
                    }

                    assert secondRow != null;
                    if (secondRow.getRowSize() < secondRow.getCapacity()) {
                        secondRow.setRowSize(+1);
                        if (secondRow.getRowSize() <= secondRow.getServers()) {
                            rowProbability(secondRow, globalTime);
                        }
                    } else {
                        secondRow.setLoss();
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

    public static void rowProbability(Row row, double globalTime) {
        double nextEvent = conversion(row.getMinService(), row.getMaxService(), randomizer());

        double v = nextEvent + globalTime;
        int result = row.getRoutings().length > 0 ? (int) row.getRoutings()[0][1] : -1;

        if (row.getRoutings().length > 0 && row.getRoutings()[0][0] < 1.0) {
            double rand = conversion(0, 1, randomizer());

            result = row.setExit(rand);
        }

        if (result >= 0) {
            events.add(new double[]{2, v, row.getId(), result});
        } else {
            events.add(new double[]{1, v, row.getId()});
        }

    }

    //static double[] r = {0.9921, 0.0004, 0.5534, 0.2761, 0.3398, 0.8963, 0.9023, 0.0132, 0.4569, 0.5121, 0.9208, 0.0171, 0.2299, 0.8545, 0.6001, 0.2921};

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