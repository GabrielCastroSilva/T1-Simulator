package com.simulator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class YamlReader {

    public Map<Integer, Rows> rows = new HashMap<>();
    public Map<Integer, Double> arrivals = new HashMap<>();
    public List<Networks> networks = new ArrayList<>();
    public List<Integer> seeds = new ArrayList<>();
    public int rndnumbersPerSeed;
    public int loops;

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

    public double[][] networksInit(int s){
        int aux = 0;
        for (Networks n : this.networks) {
            if(n.source == s) {
                aux++;
            }
        }
        double[][] routes = new double[aux][2];
        aux = 0;
        for (Networks n : this.networks) {
            if(n.source == s) {
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

    public Row[] rowsInit(){
        //double[][] routes = networksInit();
        Row[] r = new Row[this.rows.size()];
        int aux = 0;


        for (Map.Entry<Integer, Rows> entry : this.rows.entrySet()) {
            int ID = entry.getKey();
            Rows row = entry.getValue();


            Row r0 = new Row(ID, row.servers, row.capacity, row.minService, row.maxService, row.minArrival, row.maxArrival, networksInit(ID));  // Only first row, will receive the arrivals unless changed in the first event
            r[aux] = r0;
            aux++;
        }
        return r;
    }




    public static class Rows{
        public int servers;
        public int capacity = -1;
        public double minService;
        public double maxService;
        public double minArrival = 0;
        public double maxArrival = 0;
    }

    public static class Networks{
        public int source;
        public int target;
        public double probability;
    }


}
