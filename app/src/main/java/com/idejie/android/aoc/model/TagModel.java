package com.idejie.android.aoc.model;

import com.strongloop.android.loopback.Model;

import java.io.Serializable;

/**
 * Created by shandongdaxue on 16/8/17.
 */
public class TagModel extends Model implements Serializable{
    public String name;
    public String description;

    private int tagId;

    public int getTagId() {
        return tagId;
    }

    public void setTagId(int tagId) {
        this.tagId = tagId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
