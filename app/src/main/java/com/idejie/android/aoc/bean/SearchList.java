package com.idejie.android.aoc.bean;

/**
 * Created by slf on 16/8/31.
 */

public class SearchList {
    private String sort,tend,area;
    private int price;

    public SearchList() {
    }

    public SearchList(int price, String sort) {
        this.price = price;
        this.sort = sort;
    }

    public SearchList(String sort, int price, String tend, String area) {
        this.sort = sort;
        this.price = price;
        this.tend = tend;
        this.area = area;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getTend() {
        return tend;
    }

    public void setTend(String tend) {
        this.tend = tend;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }
}
