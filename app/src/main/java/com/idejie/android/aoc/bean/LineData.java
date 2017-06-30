package com.idejie.android.aoc.bean;

/**
 * Created by slf on 16/9/2.
 */

public class LineData {
    private double price;
    private String date;

    public LineData(double price, String date) {
        this.price = price;
        this.date = date;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
