package com.idejie.android.aoc.Model;

import android.widget.ListView;

import com.strongloop.android.loopback.Model;
import com.strongloop.android.loopback.ModelRepository;
import com.strongloop.android.loopback.RestAdapter;


/**
 * Created by shandongdaxue on 16/8/17.
 */

public class NewsModel extends Model {
    public static final String URL="http:192.168.1.110:3001/api";
    private ListView list;
    public String title;
    public String content;
    public String summary;
    public String created;
    public String lastUpdated;
    public Object id;
    public Object userId;
    public Object categoryId;
    public Object tagId;
    public Object imageId;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
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
    public Object getId() {
        return id;
    }

    public void setId(Object id) {
        this.id = id;
    }

    public Object getUserId() {
        return userId;
    }

    public void setUserId(Object userId) {
        this.userId = userId;
    }

    public Object getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Object categoryId) {
        this.categoryId = categoryId;
    }

    public Object getTagId() {
        return tagId;
    }

    public void setTagId(Object tagId) {
        this.tagId = tagId;
    }

    public Object getImageId() {
        return imageId;
    }

    public void setImageId(Object imageId) {
        this.imageId = imageId;
    }



}
