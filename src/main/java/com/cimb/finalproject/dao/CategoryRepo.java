package com.cimb.finalproject.dao;

import com.cimb.finalproject.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CategoryRepo extends JpaRepository<Category, Integer> {
    @Query(value = "SELECT * FROM category WHERE category= ?1", nativeQuery = true)
    public Category findByCategoryName (String categoryName);
}
