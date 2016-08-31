package com.idejie.android.aoc.model;

import com.strongloop.android.loopback.Model;

/**
 * Created by slf on 16/8/30.
 */

public class Region extends Model {
    /**
     *province (string),
     *city (string, optional),
     *description (string, optional),
     *id (number, optional)
     */
    private String province;
    private String city;
    private String description;


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
}
