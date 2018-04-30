package com.example.oliavd.bikespeedmonitor;

import java.util.*;

public class RollingAverage {

    // queue used to store list so that we get the average
    private final Queue<Double> Dataset = new LinkedList<Double>();
    private final int period;
    private double sum;

    // constructor to initialize period
    public RollingAverage(int period)
    {
        this.period = period;
    }

    // function to add new data in the
    // list and update the sum so that
    // we get the new mean
    public void addData(double num)
    {
        sum += num;
        Dataset.add(num);

        // Updating size so that length
        // of data set should be equal
        // to period as a normal mean has
        if (Dataset.size() > period)
        {
            sum -= Dataset.remove();
        }
    }

    // function to calculate mean
    public double getMean()
    {
        return sum / period;
    }
}
