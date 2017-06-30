package com.idejie.android.aoc.bean;

/**
 * Created by sxb on 16/10/20.
 */

public class RiseAndFallBean {

    private int id;
    private double price;

    public String getRiseAndFall() {
        return riseAndFall;
    }

    public void setRiseAndFall(String riseAndFall) {
        this.riseAndFall = riseAndFall;
    }

    private String riseAndFall; // 涨跌
    private String region;// 地区
    private String sort;//品种
    private String dateStr;//日期

    public String getDateStr() {
        return dateStr;
    }

    public void setDateStr(String dateStr) {
        this.dateStr = dateStr;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }



    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public int getId() {

        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
