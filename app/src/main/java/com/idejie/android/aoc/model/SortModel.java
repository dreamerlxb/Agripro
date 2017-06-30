package com.idejie.android.aoc.model;

import com.strongloop.android.loopback.Model;

/**
 * Created by slf on 16/8/30.
 */

public class SortModel extends Model {
    /**
     * name (string),
     *subName (string, optional),
     *description (string, optional),
     */
    private String name;
    private String subName;
    private String description;

    private int sortId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubName() {
        return subName;
    }

    public void setSubName(String subName) {
        this.subName = subName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getSortId() {
        return sortId;
    }

    public void setSortId(int sortId) {
        this.sortId = sortId;
    }
}
