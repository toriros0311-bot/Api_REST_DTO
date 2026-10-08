package com.lab.techstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.lab.techstore.entity.Category;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
}
