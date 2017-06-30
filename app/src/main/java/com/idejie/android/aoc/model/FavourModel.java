package com.idejie.android.aoc.model;

import com.strongloop.android.loopback.Model;

import java.util.Date;

/**
 * Created by shandongdaxue on 16/9/6.
 */

public class FavourModel extends Model {
    /**
     *created (string),
     *lastUpdated (string),
     *id (number, optional),
     *favorerId (number, optional),
     *newsId (number, optional)
     */
    private Date created,lastUpdated;
    private int favorerId,newsId;

    private int favourId;

    private NewsModel news;

    public NewsModel getNews() {
        return news;
    }

    public void setNews(NewsModel news) {
        this.news = news;
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

    public int getFavorerId() {
        return favorerId;
    }

    public void setFavorerId(int favorerId) {
        this.favorerId = favorerId;
    }

    public int getNewsId() {
        return newsId;
    }

    public void setNewsId(int newsId) {
        this.newsId = newsId;
    }

    public int getFavourId() {
        return favourId;
    }

    public void setFavourId(int favourId) {
        this.favourId = favourId;
    }
}
