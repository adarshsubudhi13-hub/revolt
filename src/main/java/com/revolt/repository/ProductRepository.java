package com.revolt.repository;

import com.revolt.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByIsActiveTrue();
    List<Product> findByCategoryIgnoreCaseAndIsActiveTrue(String category);
    List<Product> findByBrandIgnoreCaseAndIsActiveTrue(String brand);
    List<Product> findByTitleContainingIgnoreCaseAndIsActiveTrue(String title);
    List<Product> findByIdInAndIsActiveTrue(List<Long> ids);

    @Query("SELECT DISTINCT p.brand FROM Product p WHERE p.isActive = true ORDER BY p.brand")
    List<String> findDistinctBrands();

    @Query("SELECT DISTINCT p.category FROM Product p WHERE p.isActive = true ORDER BY p.category")
    List<String> findDistinctCategories();
}
