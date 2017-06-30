package com.idejie.android.aoc.model;

import com.strongloop.android.loopback.Model;

import java.io.Serializable;
import java.util.Date;


/**
 * Created by shandongdaxue on 16/8/17.
 */

public class NewsModel extends Model implements Serializable{
//    public static final String URL="http:192.168.1.110:3001/api";

    private String title;
    private String content;
    private String summary;
    private String avatarUrl;

    private Date created;
    private Date lastUpdated;

    private int userId;
    private int categoryId;
    private int tagId;
    private int imageId;
    private int newsId;

    private ImageModel avatar;
    private CategoryModel category;
    private TagModel tag;

    private int favoursCount;
    private int likersCount;
    private int commentsCount;

    private boolean isLikersCountSync;
    private boolean isCommentsCountSync;

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public boolean isLikersCountSync() {
        return isLikersCountSync;
    }

    public void setLikersCountSync(boolean likersCountSync) {
        isLikersCountSync = likersCountSync;
    }

    public boolean isCommentsCountSync() {
        return isCommentsCountSync;
    }

    public int getLikersCount() {
        return likersCount;
    }

    public void setLikersCount(int likersCount) {
        this.likersCount = likersCount;
    }

    public int getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }

    public void setCommentsCountSync(boolean commentsCountSync) {
        isCommentsCountSync = commentsCountSync;
    }

    public int getFavoursCount() {
        return favoursCount;
    }

    public void setFavoursCount(int favoursCount) {
        this.favoursCount = favoursCount;
    }

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

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getTagId() {
        return tagId;
    }

    public void setTagId(int tagId) {
        this.tagId = tagId;
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

    public ImageModel getAvatar() {
        return avatar;
    }

    public void setAvatar(ImageModel avatar) {
        this.avatar = avatar;
    }

    public CategoryModel getCategory() {
        return category;
    }

    public void setCategory(CategoryModel category) {
        this.category = category;
    }

    public TagModel getTag() {
        return tag;
    }

    public void setTag(TagModel tag) {
        this.tag = tag;
    }
}
