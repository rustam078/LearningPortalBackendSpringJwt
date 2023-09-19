package com.voicera.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.voicera.entity.ContentType;


public class ContentDTO {
    private Long contentId;
    private Integer userId;
    private Long categoryId;
    private ContentType contentType;
    private String title;
    private String url;
    @JsonInclude(Include.NON_NULL)
    private String imageUrl; // New field for image URL
    @JsonInclude(Include.NON_NULL)
    private String imageFileName; // New field for image file name
    private Date dateCreated;

    // Constructors, getters, and setters

    public ContentDTO() {
        // Default constructor
    }

    public ContentDTO(Long contentId, Integer userId, Long categoryId, ContentType contentType, String title, String url, String imageUrl, String imageFileName, Date dateCreated) {
        this.contentId = contentId;
        this.userId = userId;
        this.categoryId = categoryId;
        this.contentType = contentType;
        this.title = title;
        this.url = url;
        this.imageUrl = imageUrl;
        this.imageFileName = imageFileName;
        this.dateCreated = dateCreated;
    }

    // Getters and setters for all fields

    public Long getContentId() {
        return contentId;
    }

    public void setContentId(Long contentId) {
        this.contentId = contentId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public ContentType getContentType() {
        return contentType;
    }

    public void setContentType(ContentType contentType) {
        this.contentType = contentType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageFileName() {
        return imageFileName;
    }

    public void setImageFileName(String imageFileName) {
        this.imageFileName = imageFileName;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    @Override
    public String toString() {
        return "ContentDTO [contentId=" + contentId + ", userId=" + userId + ", categoryId=" + categoryId
                + ", contentType=" + contentType + ", title=" + title + ", url=" + url + ", imageUrl=" + imageUrl
                + ", imageFileName=" + imageFileName + ", dateCreated=" + dateCreated + "]";
    }
}
