package com.idejie.android.aoc.model;

import com.strongloop.android.loopback.Model;

import java.util.Date;

/**
 * Created by shandongdaxue on 16/9/6.
 */

public class ScrollImageModel extends Model {
    private String title;
    private Date created,lastUpdated;
    private int imageId,newsId;
    private boolean enabled;
    private int order;
    private int scrollImageId;

    private NewsModel news;

    public NewsModel getNews() {
        return news;
    }

    public void setNews(NewsModel news) {
        this.news = news;
    }

    public int getScrollImageId() {
        return scrollImageId;
    }

    public void setScrollImageId(int scrollImageId) {
        this.scrollImageId = scrollImageId;
    }

    private ImageModel image;

    public ImageModel getImage() {
        return image;
    }

    public void setImage(ImageModel image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public int getNewsId() {
        return newsId;
    }

    public void setNewsId(int newsId) {
        this.newsId = newsId;
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
