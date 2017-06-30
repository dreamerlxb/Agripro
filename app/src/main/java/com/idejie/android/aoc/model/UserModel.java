package com.idejie.android.aoc.model;

import java.util.Date;

/**
 * Created by slf on 16/9/21.
 */

public class UserModel extends com.strongloop.android.loopback.User {

    private String name;
    private int score;
    private int imageId;
    private boolean isBackEndUser;
    private String zoneCode;
    private String mobileNumber;
    private boolean mobileNumberVerified;

    private String userName;
    private Object credentials;
    private Object challenges;

    private Date created;
    private Date lastUpdated;

    private int userId;

    private ImageModel avatar;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public ImageModel getAvatar() {
        return avatar;
    }

    public void setAvatar(ImageModel avatar) {
        this.avatar = avatar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public boolean isBackEndUser() {
        return isBackEndUser;
    }

    public void setBackEndUser(boolean backEndUser) {
        isBackEndUser = backEndUser;
    }

    public String getZoneCode() {
        return zoneCode;
    }

    public void setZoneCode(String zoneCode) {
        this.zoneCode = zoneCode;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public boolean isMobileNumberVerified() {
        return mobileNumberVerified;
    }

    public void setMobileNumberVerified(boolean mobileNumberVerified) {
        this.mobileNumberVerified = mobileNumberVerified;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Object getCredentials() {
        return credentials;
    }

    public void setCredentials(Object credentials) {
        this.credentials = credentials;
    }

    public Object getChallenges() {
        return challenges;
    }

    public void setChallenges(Object challenges) {
        this.challenges = challenges;
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
}
