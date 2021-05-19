package com.simulator;

import java.util.ArrayList;
import java.util.Objects;

public class Row {

    private final int servers,capacity;
    private final double minService,maxService, minArrival, maxArrival;
    private final double[][] routing;
    private final ArrayList<Double> rowTime;
    private final int id;
    private int rowSize, loss;
    private double globalTime, totalTime;

    public Row(int id, int servers, int capacity, double minService, double maxService, double minArrival, double maxArrival, double[][] routing) {
        this.servers = servers;
        this.capacity = capacity;
        this.minService = minService;
        this.maxService = maxService;
        this.minArrival = minArrival;
        this.maxArrival = maxArrival;
        this.id = id;
        if(routing != null){ //Certifies network exists
            this.routing = routing;
        } else {
            this.routing = new double[0][0];
        }
        rowTime = new ArrayList<>();
        rowSize = 0;
        loss = 0;
        globalTime = 0.0;
        totalTime = 0.0;
    }

    public void setTime(double time) {//Updates times for row
        if(rowTime.size()<= rowSize){
            rowTime.add(0.0);
        }
        rowTime.set(rowSize, rowTime.get(rowSize) + time);
        globalTime += time;
    }

    public void resetRow(){
        totalTime += globalTime;
        globalTime = 0;
        rowSize = 0;
    }

    public void setRowSize(int extraSize) {
        rowSize += extraSize;
    }

    public int setPassage(double random){//Decide which route will be chosen
        double aux = 0.0;
        for (double[] doubles : routing) {
            aux += doubles[0];
            if (random < aux) {
                return (int) doubles[1];
            }
        }
        return -1;
    }

    public void setLoss() {
        loss++;
    }

/****************************************************************************/
/*************************Getters********************************************/
/****************************************************************************/


    public void getResults(int loops){ //Prints all details from row
        System.out.println("Row " + id);
        for (int i = 0; i < rowTime.size(); i++) {
            System.out.printf("%d\t%.4f\t%.2f%%\n", i, (rowTime.get(i)/loops), (((rowTime.get(i) * 100) / totalTime)));
        }
        for (int i = rowTime.size(); i < capacity + 1; i++) {
            System.out.printf("%d\t%.4f\t%.2f%%\n", i, 0.0, 0.0);
        }

        System.out.println("Loss Average: " + (this.loss/loops));
        System.out.println("Time average of executions: " + (totalTime/loops));
        System.out.println("Total Loss: " + (this.loss));
        System.out.println("Total time of execution: " + totalTime + "\n");
    }

    public int getServers() {
        return servers;
    }

    public int getCapacity() {
        if(this.capacity == -1){
           return Integer.MAX_VALUE;
        } else {
            return capacity;
        }
    }

    public double getMinArrival(){
        return minArrival;
    }

    public double getMaxArrival(){
        return maxArrival;
    }

    public int getId(){return id;}

    public double[][] getRoutings(){
        return routing;
    }

    public int getRowSize() {
        return rowSize;
    }

    public double getMinService() {
        return minService;
    }

    public double getMaxService() {
        return maxService;
    }

    public int getLoss() {
        return loss;
    }

    public double getTime() {
        return globalTime;
    }

}
