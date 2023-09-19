package com.voicera.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserDetailsDTO {
 
	  private Integer userId;
	    private String username;
	    private Long totalContent; // Change the type to Long
	    private Long totalVideos;
	    private Long totalArticles;
	    private Long totalCategories; // Change the type to Long

	    public UserDetailsDTO(Integer userId, String username, Long totalContent, Long totalVideos, Long totalArticles, Long totalCategories) {
	        this.userId = userId;
	        this.username = username;
	        this.totalContent = totalContent;
	        this.totalVideos = totalVideos;
	        this.totalArticles = totalArticles;
	        this.totalCategories = totalCategories;
	    }
    

   
}
