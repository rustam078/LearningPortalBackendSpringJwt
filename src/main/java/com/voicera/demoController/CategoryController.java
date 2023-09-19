package com.voicera.demoController;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.voicera.dto.CategoryDTO;
import com.voicera.dto.ContentDTO;
import com.voicera.dto.ContentDetailsDto;
import com.voicera.dto.UserDetailsDTO;
import com.voicera.entity.Content;
import com.voicera.entity.ContentType;
import com.voicera.service.ContentService;
import com.voicera.user.User;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin
public class CategoryController {
    private final ContentService contentService;

    @Autowired
    public CategoryController(ContentService contentService) {
        this.contentService = contentService;
    }

    @GetMapping("/user")
    public ResponseEntity<?> getCategoriesForLoggedInUser() {
        // Retrieve the user's ID from the security context
    	  Integer userId =null;
    	  User userDetails=null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
             userDetails = (User) authentication.getPrincipal();
           userId = userDetails.getId(); // Assuming the user class has a getId() method
        }
        String firstname = userDetails.getFirstname();
        List<CategoryDTO> categoriesByUserId = contentService.getDistinctCategoriesByUserId(userId);
        if(!categoriesByUserId.isEmpty()) {
        	  for (CategoryDTO category : categoriesByUserId) {
                  category.setFirstname(firstname);
              }
              return new ResponseEntity<>(categoriesByUserId, HttpStatus.OK);
        }
          CategoryDTO dto = new CategoryDTO();
          dto.setFirstname(firstname);
          categoriesByUserId.add(dto);
        return new ResponseEntity<>(categoriesByUserId, HttpStatus.OK);
    }

    private Optional<Integer> getUserIdFromAuthentication(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            User userDetails = (User) authentication.getPrincipal();
            Integer userId = userDetails.getId(); // Assuming the user class has a getId() method
            return Optional.of(userId);
        }
        return Optional.empty();
    }
    
    
//    @PostMapping("/add")
//    public ResponseEntity<Content> addContent(@RequestBody ContentDTO contentRequest) {
//    	  Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//          Optional<Integer> userId = getUserIdFromAuthentication(authentication);
//          contentRequest.setUserId(userId.get());
//        Content savedContent = contentService.addContent(contentRequest);
//        return new ResponseEntity<>(savedContent, HttpStatus.CREATED);
//    }
//    
    
    
    @PostMapping("/add")
    public ResponseEntity<?> addContent(@RequestBody ContentDTO contentRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<Integer> userId = getUserIdFromAuthentication(authentication);
        contentRequest.setUserId(userId.orElse(null)); // Use orElse to handle null gracefully

        // Check if the contentType is ARTICLE and process image-related fields
        if (ContentType.ARTICLE.equals(contentRequest.getContentType())) {
            // Handle image upload or URL here and set imageUrl and imageFileName accordingly
            // You can access image data from contentRequest.getImageUrl() and contentRequest.getImageFileName()
            // For example, save the image to a file system or cloud storage and set the URL and file name
//            contentRequest.setImageUrl("image_url");
//            contentRequest.setImageFileName("image_file_name");
        } else {
            // Clear image-related fields if contentType is not ARTICLE
            contentRequest.setImageUrl(null);
            contentRequest.setImageFileName(null);
        }

        Content addContent = contentService.addContent(contentRequest);
        return new ResponseEntity<>(addContent, HttpStatus.CREATED);
    }
    @GetMapping("/view")
    public ResponseEntity<?> viewContent() {
    	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    	if (authentication != null && authentication.getPrincipal() instanceof User) {
            User userDetails = (User) authentication.getPrincipal();
    	List<ContentDetailsDto> savedContent = contentService.viewContent(userDetails);
    	return new ResponseEntity<>(savedContent, HttpStatus.OK);
    }
    	return new ResponseEntity<>("No data found", HttpStatus.OK);
    }
    
    
    @GetMapping("/fetchByCategoryAndUser")
    public ResponseEntity<?> fetchContentByCategoryAndUser(@RequestParam Long categoryId) {
    	  Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
          Optional<Integer> userId = getUserIdFromAuthentication(authentication);
          List<ContentDetailsDto> fectContentDetailsDtos= contentService.getContentByCategoryAndUser(categoryId, userId.get());
      	return new ResponseEntity<>(fectContentDetailsDtos, HttpStatus.OK);
    }
    
    
    @GetMapping("/byContentType")
    public ResponseEntity<?> getContentByContentType(@RequestParam("contentType") ContentType contentType) {
     	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    	if (authentication != null && authentication.getPrincipal() instanceof User) {
            User userDetails = (User) authentication.getPrincipal();
    	List<ContentDetailsDto> savedContent = contentService.getContentByContentTypeAndUserId(contentType, userDetails);
    	return new ResponseEntity<>(savedContent, HttpStatus.OK);
    }
    	return new ResponseEntity<>("No data found", HttpStatus.OK);
    }
    
    @GetMapping("/report")
    public List<UserDetailsDTO> getUserDetails() {
  	  Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      Optional<Integer> userId = getUserIdFromAuthentication(authentication);
        return contentService.getUserDetails(userId.get());
    }
    
    
    @GetMapping("/alluser")
    public List<UserDetailsDTO> getAllUserDetails() {
        return contentService.fetchUserDetails();
    }
 
    @DeleteMapping("/deletecontent/{contentId}")
    public ResponseEntity<?> deleteContentById(@PathVariable Long contentId){
    	contentService.deleteContentById(contentId);
    	return new ResponseEntity<>("deleted sucessfully..",HttpStatus.OK);
    }
    
    
    @PutMapping("/updateContent/{contentId}")
    public ResponseEntity<Content> updateContent(
        @PathVariable Long contentId, 
        @RequestBody Content updatedArticle) {
        
    	 Optional<Content> existingContent = contentService.findById(contentId);
        
        if (existingContent.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        // Update only the provided fields
        if (updatedArticle.getTitle() != null) {
        	existingContent.get().setTitle(updatedArticle.getTitle());
        }
        
        if (updatedArticle.getUrl() != null) {
        	existingContent.get().setUrl(updatedArticle.getUrl());
        }
        
        Content savedArticle = contentService.updateContent(existingContent.get());
        return ResponseEntity.ok(savedArticle);
    }
}
