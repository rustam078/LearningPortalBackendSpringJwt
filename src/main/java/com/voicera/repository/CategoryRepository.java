package com.voicera.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.voicera.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    // You can define custom query methods here if needed
}

