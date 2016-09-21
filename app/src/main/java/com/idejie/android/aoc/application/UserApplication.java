package com.idejie.android.aoc.application;

import android.app.Application;

/**
 * Created by slf on 16/9/21.
 */

public class UserApplication extends Application{
    private String id;
    private String score;
    private String name;
    private int imageId;

    public void onCreate(){
        super.onCreate();
        setId("0");
        setName("用户");
        setImageId(0);
        setScore("0");
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
