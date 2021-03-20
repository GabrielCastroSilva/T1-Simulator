package com.simulator;

public class Main {
    //Values of the congruential calculus (a * x(i) + c) % m
    public static int a = 25173;
    public static int c = 13849;
    public static int m = 32768;
    public static double x = m/2;


    public static double randomizer(){
        x = (a * x + c) % m;

        return x/m;
    }


    public static void main(String[] args) {
        int count = 100; //Amount of random generated numbers

        for(int i = 0 ; i < count ; i++){
            System.out.println(randomizer());
        }
    }
}
