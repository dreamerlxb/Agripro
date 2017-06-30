package com.idejie.android.aoc.model;

import com.strongloop.android.loopback.Model;

import java.util.Date;

/**
 * Created by sxb on 16/10/14.
 */

public class LikeModel extends Model {
    private Date created;
    private Date lastUpdated;
    private int likerId;
    private int newsId;

    private int likeId;

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

    public int getLikerId() {
        return likerId;
    }

    public void setLikerId(int likerId) {
        this.likerId = likerId;
    }

    public int getLikeId() {
        return likeId;
    }

    public void setLikeId(int likeId) {
        this.likeId = likeId;
    }

    public int getNewsId() {
        return newsId;
    }

    public void setNewsId(int newsId) {
        this.newsId = newsId;
    }

}
