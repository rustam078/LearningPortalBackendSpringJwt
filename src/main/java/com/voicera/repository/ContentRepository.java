package com.voicera.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.voicera.dto.CategoryDTO;
import com.voicera.dto.ContentDTO;
import com.voicera.entity.Content;
import com.voicera.entity.ContentType;
import com.voicera.user.User;

public interface ContentRepository extends JpaRepository<Content, Long> {

	@Query("SELECT DISTINCT NEW com.voicera.dto.CategoryDTO(C.category.id AS categoryId, CAT.name AS categoryName) FROM Content C JOIN C.category CAT WHERE C.user.id = :userId")
	List<CategoryDTO> findDistinctCategoriesByUserId(Integer userId);

	List<Content> findByUserId(Integer userId);
	
	List<Content> findByCategoryCategoryIdAndUserId(Long categoryId, Integer userId);
	
//	 List<ContentDTO> findByContentType(ContentType contentType);

	List<Content> findByContentTypeAndUserId(ContentType contentType, Integer userId);
}
