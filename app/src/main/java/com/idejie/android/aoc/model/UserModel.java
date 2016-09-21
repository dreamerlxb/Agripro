package com.idejie.android.aoc.model;

/**
 * Created by slf on 16/9/21.
 */

public class UserModel extends com.strongloop.android.loopback.User {
    public String name;
    public Object score;
    private int imageId;

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getScore() {
        return score;
    }

    public void setScore(Object score) {
        this.score = score;
    }
}
