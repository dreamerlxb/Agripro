package com.idejie.android.aoc.bean;

/**
 * Created by slf on 16/8/31.
 */

public class SearchList {
    private String sort,price,tend,area;

    public SearchList() {
    }

    public SearchList(String sort, String price, String tend, String area) {
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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
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
