package com.simulator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class YamlReader {
    //This class follows Jackson's and SnakeYaml guidelines
    public Map<Integer, Rows> rows = new HashMap<>();
    public Map<Integer, Double> arrivals = new HashMap<>();
    public List<Networks> networks = new ArrayList<>();
    public List<Integer> seeds = new ArrayList<>();
    public int rndnumbersPerSeed;
    public int loops;

    public static class Rows{//Class object for rows
        public int servers;
        public int capacity = -1;
        public double minService;
        public double maxService;
        public double minArrival = 0;
        public double maxArrival = 0;
    }

    public static class Networks{//Class object for networks
        public int source;
        public int target;
        public double probability;
    }

    public List<Integer> getSeeds() {
        return seeds;
    }

    public int getRndnumbersPerSeed(){
        return rndnumbersPerSeed;
    }

    public int getLoops(){
        return loops;
    }

    public Map<Integer,Double> getArrivals(){
        return arrivals;
    }

    public double[][] networksInit(int source){//Returns every network from a given source
        int aux = 0;
        for (Networks n : this.networks) {//Finds number of networks
            if(n.source == source) {
                aux++;
            }
        }
        double[][] routes = new double[aux][2];
        aux = 0;
        for (Networks n : this.networks) {//Populates array of routes
            if(n.source == source) {
                routes[aux][0] = n.probability;
                routes[aux][1] = n.target;
                aux++;
            }
        }
        return routes;
    }

    /*
            String id, // ID used for routing
            int servers,
            int capacity,  // if -1 then its maximum integer limit (no capacity limit)
            double minService,
            double maxService
            double minArrival,
            double maxArrival
            double[][] routing {probability, position in array of destination}
     */

    public Row[] rowsInit(){//Returns every row from yml file
        Row[] r = new Row[this.rows.size()];
        int aux = 0;

        for (Map.Entry<Integer, Rows> entry : this.rows.entrySet()) {//Populates row array
            int ID = entry.getKey();
            Rows row = entry.getValue();

            Row r0 = new Row(ID, row.servers, row.capacity, row.minService, row.maxService, row.minArrival, row.maxArrival, networksInit(ID));  // Only first row, will receive the arrivals unless changed in the first event
            r[aux] = r0;
            aux++;
        }
        return r;
    }


}
