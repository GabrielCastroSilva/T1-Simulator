package com.simulator;

import java.util.ArrayList;

public class Row {

    private final int servers,capacity;
    private final double minService,maxService;
    private ArrayList<Double> rowTime;
    private String id;
    private int rowSize, loss;
    private double globalTime;

    public Row(String id, int servers, int capacity, double minService, double maxService) {
        this.servers = servers;
        this.capacity = capacity;
        this.minService = minService;
        this.maxService = maxService;
        this.id = id;
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

    public void setLoss() {
        loss++;
    }

/****************************************************************************/
/*************************Getters********************************************/
/****************************************************************************/
    public int getServers() {
        return servers;
    }

    public int getCapacity() {
        return capacity;
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

    public int getRowSize() {
        return rowSize;
    }
}
