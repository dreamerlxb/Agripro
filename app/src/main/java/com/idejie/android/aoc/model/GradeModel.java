package com.idejie.android.aoc.model;

import com.strongloop.android.loopback.Model;

/**
 * Created by slf on 16/8/30.
 */

public class GradeModel extends Model {
    /**
     * name (string),
     *description (string, optional),
     *id (number, optional),
     *sortId (number, optional)
     */
    private String name;
    private int sortId ;
    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSortId() {
        return sortId;
    }

    public void setSortId(int sortId) {
        this.sortId = sortId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


}
