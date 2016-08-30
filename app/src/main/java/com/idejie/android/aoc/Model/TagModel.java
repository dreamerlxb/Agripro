package com.idejie.android.aoc.Model;

import com.strongloop.android.loopback.Model;

/**
 * Created by shandongdaxue on 16/8/17.
 */
public class TagModel extends Model {
    public String name;
    public String description;
    public Object id;

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

    @Override
    public Object getId() {
        return id;
    }

    public void setId(Object id) {
        this.id = id;
    }
}
