package com.voicera.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.voicera.dto.UserDetailsDTO;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;


@Repository
public class UserDetailsRepository  {

	@PersistenceContext
	private EntityManager em;
	
	
	 public  List<UserDetailsDTO> fetchUserDetails(){
		String jpqlQuery = "SELECT new com.voicera.dto.UserDetailsDTO(" +
		        "u.id, " +
		        "u.firstname, " +
		        "COUNT(DISTINCT c.contentId), " +
		        "SUM(CASE WHEN c.contentType = 'VIDEO' THEN 1 ELSE 0 END), " +
		        "SUM(CASE WHEN c.contentType = 'ARTICLE' THEN 1 ELSE 0 END), " +
		        "COUNT(DISTINCT c.category.categoryId)) " + // Use c.category.categoryId to access the category ID
		        "FROM User u " +
		        "LEFT JOIN Content c ON u.id = c.user.id " + // Use c.user.id to access the user's ID
		        "GROUP BY u.id, u.firstname";
			 TypedQuery<UserDetailsDTO> query = em.createQuery(jpqlQuery, UserDetailsDTO.class);
			 List<UserDetailsDTO> userDetailsList = query.getResultList();

			return  userDetailsList;
	   }
	   
}
