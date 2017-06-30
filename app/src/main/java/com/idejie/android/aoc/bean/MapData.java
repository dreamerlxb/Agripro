package com.idejie.android.aoc.bean;

/**
 * Created by slf on 16/9/2.
 */

public class MapData {
    private String name;
    private double value;

    public MapData(String name, double value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
