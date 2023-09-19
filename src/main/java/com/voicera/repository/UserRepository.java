package com.voicera.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.voicera.user.User;

public interface UserRepository extends JpaRepository<User, Integer> {

	Optional<User> findByEmail(String email);

	Boolean existsByEmail(String email);
	
	 

}
