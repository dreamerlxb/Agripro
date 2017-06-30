package com.idejie.android.aoc.model;


import com.strongloop.android.loopback.Model;

import java.util.Date;

/**
 * Created by shandongdaxue on 16/9/6.
 */

public class CommentModel extends Model {
    /**
     *content (string, optional),
     *created (string),
     *lastUpdated (string),
     *id (number, optional),
     *newsId (number, optional),
     *authorId (number, optional)
     */
    private String content;
    private int newsId;
    private int authorId;
    private Date created;
    private Date lastUpdated;

    private UserModel author;

    private int commentId;
    private NewsModel news;

    public NewsModel getNews() {
        return news;
    }

    public void setNews(NewsModel news) {
        this.news = news;
    }

    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }


    public UserModel getAuthor() {
        return author;
    }

    public void setAuthor(UserModel author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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


    public int getNewsId() {
        return newsId;
    }

    public void setNewsId(int newsId) {
        this.newsId = newsId;
    }

    public int getAuthorId() {
        return authorId;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }
}
