package com.idejie.android.aoc.Model;

import com.strongloop.android.loopback.Model;

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
    private double price,turnover;
    private String marketName,priceDate,created,lastUpdated;
    private int UserId,regoinId,sortId,gradeId;

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

    public String getPriceDate() {
        return priceDate;
    }

    public void setPriceDate(String priceDate) {
        this.priceDate = priceDate;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }



    public int getUserId() {
        return UserId;
    }

    public void setUserId(int userId) {
        UserId = userId;
    }

    public int getRegoinId() {
        return regoinId;
    }

    public void setRegoinId(int regoinId) {
        this.regoinId = regoinId;
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
}
