package com.idejie.android.aoc.bean;

/**
 * Created by slf on 16/8/31.
 */

public class TendList {
    private String area,sort,rank,price,date;

    public TendList(String area, String sort, String rank, String price,  String date) {
        this.area = area;
        this.sort = sort;
        this.rank = rank;
        this.price = price;
        this.date = date;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
