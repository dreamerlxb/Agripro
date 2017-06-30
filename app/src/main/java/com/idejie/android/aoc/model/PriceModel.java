package com.idejie.android.aoc.model;

import com.strongloop.android.loopback.Model;

import java.util.Date;

/**
 * Created by slf on 16/8/28.
 */

public class PriceModel extends Model {
    /**
     * price (number, optional),
     turnover (number, optional),
     marketName (string, optional),
     priceDate (string),
     created (string),
     lastUpdated (string),
     id (number, optional),
     userId (number, optional),
     regionId (number, optional),
     sortId (number, optional),
     gradeId (number, optional)
     */
    private double price;
    private double turnover;
    private String marketName;

    private Date priceDate;
    private Date created;
    private Date lastUpdated;

    private int userId,regionId,sortId,gradeId;
    private int priceId;

    private RegionModel region;
    private SortModel sort;
    private GradeModel grade;


    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getTurnover() {
        return turnover;
    }

    public void setTurnover(double turnover) {
        this.turnover = turnover;
    }

    public String getMarketName() {
        return marketName;
    }

    public void setMarketName(String marketName) {
        this.marketName = marketName;
    }

    public Date getPriceDate() {
        return priceDate;
    }

    public void setPriceDate(Date priceDate) {
        this.priceDate = priceDate;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getRegionId() {
        return regionId;
    }

    public void setRegionId(int regionId) {
        this.regionId = regionId;
    }

    public int getSortId() {
        return sortId;
    }

    public void setSortId(int sortId) {
        this.sortId = sortId;
    }

    public int getGradeId() {
        return gradeId;
    }

    public void setGradeId(int gradeId) {
        this.gradeId = gradeId;
    }

    public int getPriceId() {
        return priceId;
    }

    public void setPriceId(int priceId) {
        this.priceId = priceId;
    }

    public RegionModel getRegion() {
        return region;
    }

    public void setRegion(RegionModel region) {
        this.region = region;
    }

    public SortModel getSort() {
        return sort;
    }

    public void setSort(SortModel sort) {
        this.sort = sort;
    }

    public GradeModel getGrade() {
        return grade;
    }

    public void setGrade(GradeModel grade) {
        this.grade = grade;
    }
}
