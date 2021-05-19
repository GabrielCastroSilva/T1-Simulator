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
    private static int randomCount; //Number to break while loop
    private static PriorityQueue<double[]> events;
    private static List<Integer> seeds;
    private static int loops; //Number of times that the simulation will run
    private static Map<Integer, Double> arrivalTimes;


    public static Row[] initializer(String fileName) throws IOException {
         /*
        String id, // ID used for routing
        int servers,
        int capacity,  // if -1 then its maximum integer limit (no capacity limit)
        double minService,
        double maxService,
        double minArrival,
        double maxArrival,
        double[][] routing {probability, position in array of destination}
         */

        ObjectMapper factory = new ObjectMapper(new YAMLFactory()); //Loads Jackson Yaml methods
        YamlReader reader = factory.readValue(new File(fileName), YamlReader.class); //Read the file using YamlReader class

        //Loads values in variables
        seeds = reader.getSeeds();
        loops = reader.getLoops();
        count = reader.getRndnumbersPerSeed();
        arrivalTimes = reader.getArrivals();

        return reader.rowsInit(); //executes rowsInit method

    }


    public static void main(String[] args) throws IOException { // G/G/2/3   Geometric distribution of arrival and attendance with 2 server 3 capacity
        Row[] rows = initializer("model.yml"); // Init for row array

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

            for (Map.Entry<Integer, Double> entry : arrivalTimes.entrySet()) { //Populates event list with initial arrivals
                int pos = entry.getKey();
                double time = entry.getValue();
                events.add(new double[]{0, time, pos}); //First arrival //Reading is .get(listPosition)[arrayPosition]
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

                    globalTime = lowest[1]; //Updates globalTime

                    assert row != null;
                    if (row.getRowSize() < row.getCapacity()) { //Checks if there is capacity for an arrival

                        row.setRowSize(+1);

                        if (row.getRowSize() <= row.getServers()) { //Checks if an event can be scheduled
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
                    for (Row r : rows) { //Time contabilization
                        r.setTime(lowest[1] - globalTime);
                        if (r.getId() == (int) lowest[2]) {
                            row = r;
                        }
                    }

                    assert row != null;
                    globalTime = lowest[1];
                    row.setRowSize(-1);

                    if (row.getRowSize() >= row.getServers()) {
                        rowProbability(row, globalTime);

                    }

                } else if (lowest[0] == 2) { // Passage
                    Row row = null;
                    Row secondRow = null;

                    for (Row r : rows) { //Time contabilization
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


                    if (row.getRowSize() >= row.getServers()) { //Checks if first row can have an event
                        rowProbability(row, globalTime);
                    }

                    assert secondRow != null;
                    if (secondRow.getRowSize() < secondRow.getCapacity()) { //Checks if second row can receive an appointment
                        secondRow.setRowSize(+1);
                        if (secondRow.getRowSize() <= secondRow.getServers()) { //Checks if a second row can receive an event
                            rowProbability(secondRow, globalTime);
                        }
                    } else {
                        secondRow.setLoss();
                    }
                }
            }
            events = new PriorityQueue<>(comparator); //Empties events queue
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
        double nextEvent = conversion(row.getMinService(), row.getMaxService(), randomizer()); //Generates next random number

        double v = nextEvent + globalTime;
        int result;

        if (row.getRoutings().length > 0) { //Decides if event will be a passage or an exit
            result = (int) row.getRoutings()[0][1]; //Exit
        } else {
            result = -1; //Passage
        }

        if (row.getRoutings().length > 0 && row.getRoutings()[0][0] < 1.0) { //If passage generate number to decide which row to go
            double rand = conversion(0, 1, randomizer());

            result = row.setPassage(rand);
        }

        if (result >= 0) {
            events.add(new double[]{2, v, row.getId(), result}); //Passage
        } else {
            events.add(new double[]{1, v, row.getId()}); //Exit
        }

    }

    //static double[] r = {0.9921, 0.0004, 0.5534, 0.2761, 0.3398, 0.8963, 0.9023, 0.0132, 0.4569, 0.5121, 0.9208, 0.0171, 0.2299, 0.8545, 0.6001, 0.2921};

    //Method that executes the linear congruential calculation
    public static double randomizer() {
        randomCount++;
        x = (a * x + c) % m;

        //return r[randomCount];

        return x / m;
    }

    public static double conversion(double A, double B, double random) {
        return (B - A) * (random) + A;
    }

}