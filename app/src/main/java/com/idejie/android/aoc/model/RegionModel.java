package com.idejie.android.aoc.model;

import com.strongloop.android.loopback.Model;

/**
 * Created by slf on 16/8/30.
 */

public class RegionModel extends Model {
    /**
     *province (string),
     *city (string, optional),
     *description (string, optional),
     *id (number, optional)
     */
    private String province;
    private String city;
    private String description;

    private int regionId;


    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getRegionId() {
        return regionId;
    }

    public void setRegionId(int regionId) {
        this.regionId = regionId;
    }
}
