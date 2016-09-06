package com.idejie.android.aoc.model;


import com.strongloop.android.loopback.Model;

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
    public String content,created,lastUpdated,id,newsID,authorID;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public String getAuthorID() {
        return authorID;
    }

    public void setAuthorID(String authorID) {
        this.authorID = authorID;
    }

    public String getNewsID() {
        return newsID;
    }

    public void setNewsID(String newsID) {
        this.newsID = newsID;
    }
}
