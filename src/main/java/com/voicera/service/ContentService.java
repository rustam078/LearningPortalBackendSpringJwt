package com.voicera.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.voicera.dto.CategoryDTO;
import com.voicera.dto.ContentDTO;
import com.voicera.dto.ContentDetailsDto;
import com.voicera.dto.UserDetailsDTO;
import com.voicera.entity.Category;
import com.voicera.entity.Content;
import com.voicera.entity.ContentType;
import com.voicera.repository.CategoryRepository;
import com.voicera.repository.ContentRepository;
import com.voicera.repository.UserDetailsRepository;
import com.voicera.repository.UserRepository;
import com.voicera.user.User;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ContentService {
    private final ContentRepository contentRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private UserDetailsRepository userdetailsrepo;
    @Autowired
    public ContentService(ContentRepository contentRepository) {
        this.contentRepository = contentRepository;
    }

    public List<CategoryDTO> getDistinctCategoriesByUserId(Integer userId) {
        return contentRepository.findDistinctCategoriesByUserId(userId);
    }
    
    
//    public Content addContent(ContentDTO contentRequest) {
//        Content content = new Content();
//        content.setTitle(contentRequest.getTitle());
//        content.setUrl(contentRequest.getUrl());
//        content.setContentType(contentRequest.getContentType());
//        content.setDateCreated(new Date());
//
//        // Assuming you have methods to fetch User and Category based on IDs
//        User user = getUserById(contentRequest.getUserId());
//        Category category = getCategoryById(contentRequest.getCategoryId());
//
//        content.setUser(user);
//        content.setCategory(category);
//
//        return contentRepository.save(content);
//    }
    
    public Content addContent(ContentDTO contentRequest) {
        Content content = new Content();
        content.setTitle(contentRequest.getTitle());
        content.setUrl(contentRequest.getUrl());
        content.setContentType(contentRequest.getContentType());
        content.setDateCreated(new Date());
        content.setImageFileName(contentRequest.getImageFileName());
        content.setImageUrl(contentRequest.getImageUrl());

        // Fetch User and Category based on IDs
        User user = userRepository.findById(contentRequest.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + contentRequest.getUserId()));

        Category category = categoryRepository.findById(contentRequest.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found with ID: " + contentRequest.getCategoryId()));

        content.setUser(user);
        content.setCategory(category);

        return contentRepository.save(content);
    }

    
//    private User getUserById(Integer userId) {
//        Optional<User> userOptional = userRepository.findById(userId);
//        return userOptional.orElse(null); // Return null or handle the absence of the user entity as needed
//    }
//    
//    private Category getCategoryById(Long categoryId) {
//        Optional<Category> categoryOptional = categoryRepository.findById(categoryId);
//        return categoryOptional.orElse(null); // Return null or handle the absence of the category entity as needed
//    }

	public List<ContentDetailsDto> viewContent(User userDetails) {
		 List<Content> contentListByUserId = contentRepository.findByUserId(userDetails.getId());
		 List<ContentDetailsDto> contentDetailsList = contentListByUserId.stream()
				    .map(content -> new ContentDetailsDto(
				        content.getContentId(),
				        content.getCategory().getCategoryId(),
				        content.getCategory().getName(),
				        content.getContentType().toString(), // Assuming ContentType is an Enum
				        content.getTitle(),
				        content.getUrl(),
				        content.getImageUrl(),
				        content.getImageFileName(),
				        content.getDateCreated()
				    ))
				    .collect(Collectors.toList());
		 return contentDetailsList;
	}
	
	
	  public List<ContentDetailsDto> getContentByCategoryAndUser(Long categoryId, Integer userId) {
	       List<Content> idAndUserId = contentRepository.findByCategoryCategoryIdAndUserId(categoryId, userId);
	       List<ContentDetailsDto> contentDetailsList = idAndUserId.stream()
				    .map(content -> new ContentDetailsDto(
				        content.getContentId(),
				        content.getCategory().getCategoryId(),
				        content.getCategory().getName(),
				        content.getContentType().toString(), // Assuming ContentType is an Enum
				        content.getTitle(),
				        content.getUrl(),
				        content.getImageUrl(),
				        content.getImageFileName(),
				        content.getDateCreated()
				    ))
				    .collect(Collectors.toList());
		 return contentDetailsList;
	    }
	  
	  

	public List<ContentDetailsDto> getContentByContentTypeAndUserId(ContentType contentType, User userDetails) {
		
		 List<Content> contentListByUserId = contentRepository.findByContentTypeAndUserId(contentType, userDetails.getId());
		 List<ContentDetailsDto> contentDetailsList = contentListByUserId.stream()
				    .map(content -> new ContentDetailsDto(
				        content.getContentId(),
				        content.getCategory().getCategoryId(),
				        content.getCategory().getName(),
				        content.getContentType().toString(), // Assuming ContentType is an Enum
				        content.getTitle(),
				        content.getUrl(),
				        content.getImageUrl(),
				        content.getImageFileName(),
				        content.getDateCreated()
				    ))
				    .collect(Collectors.toList());
		 return contentDetailsList;
	}
	
	
	
	 public List<UserDetailsDTO> getUserDetails(Integer userId) {
	        List<User> users;
	        if (userId != null) {
	            users = userRepository.findAllById(List.of(userId));
	        } else {
	            users = userRepository.findAll();
	        }

	         List<UserDetailsDTO> collect = users.stream()
	                .map(user -> {
	                    List<Content> userContents = contentRepository.findByUserId(user.getId());
	                    long totalVideos = userContents.stream().filter(content -> ContentType.VIDEO.equals(content.getContentType())).count();
	                    long totalArticles = userContents.stream().filter(content -> ContentType.ARTICLE.equals(content.getContentType())).count();
	                    long totalCategories = userContents.stream().map(content -> content.getCategory().getCategoryId()).distinct().count();
	                    int size = userContents.size();
	                    return new UserDetailsDTO(
	                            user.getId(),
	                            user.getFirstname(),
	                            (long) userContents.size(),
	                            totalVideos,
	                            totalArticles,
	                            totalCategories
	                    );
	                })
	                .collect(Collectors.toList());
	         return collect;
	    }
	 
	 
	
	    public List<UserDetailsDTO> fetchUserDetails() {
	        return userdetailsrepo.fetchUserDetails();
	    }
	    
    public void deleteContentById(Long contentId) {
    	contentRepository.deleteById(contentId);
    }
    
    public Optional<Content> findById(Long contentId) {
    	return contentRepository.findById(contentId);
    }

	public Content updateContent(Content content) {
		return contentRepository.save(content);
	}
}
