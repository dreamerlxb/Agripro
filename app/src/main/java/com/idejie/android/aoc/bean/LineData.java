package com.idejie.android.aoc.bean;

/**
 * Created by slf on 16/9/2.
 */

public class LineData {
    private int price;
    private String date;

    public LineData(int price, String date) {
        this.price = price;
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
