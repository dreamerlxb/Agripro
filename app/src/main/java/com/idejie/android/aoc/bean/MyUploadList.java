package com.idejie.android.aoc.bean;

/**
 * Created by slf on 16/8/31.
 */

public class MyUploadList {
    private String sort;
    private int amount;
    private String marketName;
    private String area;
    private String upDate;
    private int price;

    public MyUploadList(String sort, int amount, String marketName, String area, String upDate, int price) {
        this.sort = sort;
        this.amount = amount;
        this.marketName = marketName;
        this.area = area;
        this.upDate = upDate;
        this.price = price;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getMarketName() {
        return marketName;
    }

    public void setMarketName(String marketName) {
        this.marketName = marketName;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getUpDate() {
        return upDate;
    }

    public void setUpDate(String upDate) {
        this.upDate = upDate;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
