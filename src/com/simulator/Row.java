package com.simulator;

import java.util.ArrayList;

public class Row {

    private final int servers,capacity;
    private final double minService,maxService;
    private final double[][] routing;
    private ArrayList<Double> rowTime;
    private int id;
    private int rowSize, loss;
    private double globalTime;

    public Row(int id, int servers, int capacity, double minService, double maxService, double[][] routing) {
        this.servers = servers;
        this.capacity = capacity;
        this.minService = minService;
        this.maxService = maxService;
        this.id = id;
        if (routing != null && routing.length == 2 && routing[0].length == routing[1].length){
            this.routing = routing;
        } else {
            this.routing = new double[2][0];
        }
        rowTime = new ArrayList<>();
        rowSize = 0;
        loss = 0;
        globalTime = 0.0;
    }

    public void setTime(double time) {
        if(rowTime.size()<= rowSize){
            rowTime.add(0.0);
        }
        rowTime.set(rowSize, rowTime.get(rowSize) + time);
        globalTime += time;
    }

    public void setRowSize(int extraSize) {
        rowSize += extraSize;
    }

    public int setExit(double random){
        double aux = 0.0;
        for(int i = 0 ; i < routing[0].length ; i++){
            aux += routing[i][0];
            if(random < aux){
                return (int) routing[i][1];
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


    public void getResults(){
        System.out.println("Row " + id);
        for (int i = 0; i < rowTime.size(); i++) {
            System.out.printf("%d\t%.4f\t%.2f%%\n", i, rowTime.get(i), ((rowTime.get(i) * 100) / globalTime));
        }
        for (int i = rowTime.size(); i < capacity + 1; i++) {
            System.out.printf("%d\t%.4f\t%.2f%%\n", i, 0.0, 0.0);
        }

        System.out.println("Loss: " + getLoss());
        System.out.println("Total time: " + getTime() + "\n");
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
