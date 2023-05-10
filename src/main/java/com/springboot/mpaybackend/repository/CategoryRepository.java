package com.springboot.mpaybackend.repository;

import com.springboot.mpaybackend.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
