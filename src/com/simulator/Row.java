package com.simulator;

import java.util.List;

public class Row {

    private int servers;
    private int capacity;
    private List<Integer> row;
    private double minArrival;
    private double maxArrival;
    private double minService;
    private double maxService;

    private List<Double> time;
    private double totalTime;

    public Row(int servers, int capacity, List<Integer> row, double minArrival, double maxArrival, double minService, double maxService){
        this.servers = servers;
        this.capacity = capacity;
        this.row = row;
        this.minArrival = minArrival;
        this.maxArrival = maxArrival;
        this.minService = minService;
        this.maxService = maxService;
    }

    public List<Double> getTime() {
        return time;
    }

    public void setTime(List<Double> time) {
        this.time = time;
    }

    public double getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(double totalTime) {
        this.totalTime = totalTime;
    }

    public int getServers() {
        return servers;
    }

    public void setServers(int servers) {
        this.servers = servers;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public List<Integer> getRow() {
        return row;
    }

    public void setRow(List<Integer> row) {
        this.row = row;
    }

    public double getMinArrival() {
        return minArrival;
    }

    public void setMinArrival(double minArrival) {
        this.minArrival = minArrival;
    }

    public double getMaxArrival() {
        return maxArrival;
    }

    public void setMaxArrival(double maxArrival) {
        this.maxArrival = maxArrival;
    }

    public double getMinService() {
        return minService;
    }

    public void setMinService(double minService) {
        this.minService = minService;
    }

    public double getMaxService() {
        return maxService;
    }

    public void setMaxService(double maxService) {
        this.maxService = maxService;
    }
}
