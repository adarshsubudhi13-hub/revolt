package com.revolt.service;

import com.revolt.model.Product;
import com.revolt.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAllActiveProducts() {
        return productRepository.findByIsActiveTrue();
    }

    public List<Product> filterProducts(String category, String brand, Double minPrice, Double maxPrice, String search, String sort) {
        List<Product> list = productRepository.findByIsActiveTrue();

        if (category != null && !category.trim().isEmpty() && !"ALL".equalsIgnoreCase(category)) {
            list = list.stream()
                    .filter(p -> p.getCategory().equalsIgnoreCase(category.trim()))
                    .collect(Collectors.toList());
        }

        if (brand != null && !brand.trim().isEmpty() && !"ALL".equalsIgnoreCase(brand)) {
            list = list.stream()
                    .filter(p -> p.getBrand().equalsIgnoreCase(brand.trim()))
                    .collect(Collectors.toList());
        }

        if (minPrice != null && minPrice > 0) {
            list = list.stream()
                    .filter(p -> p.getPrice() >= minPrice)
                    .collect(Collectors.toList());
        }

        if (maxPrice != null && maxPrice > 0) {
            list = list.stream()
                    .filter(p -> p.getPrice() <= maxPrice)
                    .collect(Collectors.toList());
        }

        if (search != null && !search.trim().isEmpty()) {
            String q = search.trim().toLowerCase();
            list = list.stream()
                    .filter(p -> p.getTitle().toLowerCase().contains(q) ||
                                 (p.getDescription() != null && p.getDescription().toLowerCase().contains(q)) ||
                                 p.getBrand().toLowerCase().contains(q))
                    .collect(Collectors.toList());
        }

        if ("price_asc".equalsIgnoreCase(sort)) {
            list.sort(Comparator.comparing(Product::getPrice));
        } else if ("price_desc".equalsIgnoreCase(sort)) {
            list.sort(Comparator.comparing(Product::getPrice).reversed());
        } else if ("rating".equalsIgnoreCase(sort)) {
            list.sort(Comparator.comparing(Product::getRating).reversed());
        } else {
            // default: id desc / newest
            list.sort(Comparator.comparing(Product::getId).reversed());
        }

        return list;
    }

    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategoryIgnoreCaseAndIsActiveTrue(category);
    }

    public List<Product> searchProducts(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllActiveProducts();
        }
        return productRepository.findByTitleContainingIgnoreCaseAndIsActiveTrue(query.trim());
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public List<Product> getProductsForComparison(List<Long> ids) {
        return productRepository.findByIdInAndIsActiveTrue(ids);
    }

    public List<String> getDistinctBrands() {
        return productRepository.findDistinctBrands();
    }

    public List<String> getDistinctCategories() {
        return productRepository.findDistinctCategories();
    }

    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }
}
