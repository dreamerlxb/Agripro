package com.idejie.android.aoc.model;

import com.strongloop.android.loopback.Model;

/**
 * Created by shandongdaxue on 16/9/6.
 */

public class ScrollImageModel extends Model {
    /**
     * title (string),
     enabled (boolean, optional),
     order (number, optional),
     created (string, optional),
     lastUpdated (string, optional),
     id (number, optional),
     imageId (number, optional),
     newsId (number, optional)
     */
    public String title,created,lastUpdated,id,imageID,newsID;
    public boolean enabled;
    public int order;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

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

    public String getImageID() {
        return imageID;
    }

    public void setImageID(String imageID) {
        this.imageID = imageID;
    }

    public String getNewsID() {
        return newsID;
    }

    public void setNewsID(String newsID) {
        this.newsID = newsID;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
