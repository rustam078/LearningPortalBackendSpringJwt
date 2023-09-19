package com.voicera.dto;

import java.util.Date;

public class ContentDetailsDto {
    private Long contentId;
    private Long categoryId;
    private String name;
    private String contentType;
    private String title;
    private String url;
    private String imageUrl; 
    private String imageFileName; 
    private Date dateCreated;
	public ContentDetailsDto() {
		super();
		// TODO Auto-generated constructor stub
	}
	public ContentDetailsDto(Long contentId, Long categoryId, String name, String contentType, String title, String url,
			String imageUrl, String imageFileName, Date dateCreated) {
		super();
		this.contentId = contentId;
		this.categoryId = categoryId;
		this.name = name;
		this.contentType = contentType;
		this.title = title;
		this.url = url;
		this.imageUrl = imageUrl;
		this.imageFileName = imageFileName;
		this.dateCreated = dateCreated;
	}
	public Long getContentId() {
		return contentId;
	}
	public void setContentId(Long contentId) {
		this.contentId = contentId;
	}
	public Long getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
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
		return "ContentDetailsDto [contentId=" + contentId + ", categoryId=" + categoryId + ", name=" + name
				+ ", contentType=" + contentType + ", title=" + title + ", url=" + url + ", imageUrl=" + imageUrl
				+ ", imageFileName=" + imageFileName + ", dateCreated=" + dateCreated + "]";
	}

}
