package com.idejie.android.aoc.model;

import com.strongloop.android.loopback.Model;

/**
 * Created by shandongdaxue on 16/9/6.
 */

public class FavorModel extends Model {
    /**
     *created (string),
     *lastUpdated (string),
     *id (number, optional),
     *favorerId (number, optional),
     *newsId (number, optional)
     */
    public String created,lastUpdated,id,favoredID,newsID;

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFavoredID() {
        return favoredID;
    }

    public void setFavoredID(String favoredID) {
        this.favoredID = favoredID;
    }

    public String getNewsID() {
        return newsID;
    }

    public void setNewsID(String newsID) {
        this.newsID = newsID;
    }
}
