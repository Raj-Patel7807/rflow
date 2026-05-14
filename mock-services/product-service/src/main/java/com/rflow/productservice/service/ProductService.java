package com.rflow.productservice.service;

import com.rflow.productservice.exception.ProductNotFoundException;
import com.rflow.productservice.model.Product;
import com.rflow.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id).orElseThrow(
                () -> new ProductNotFoundException("Product Not Found with Id: " + id)
        );
    }

    public Product createProduct(Product product) {
        product.setCreatedAt(LocalDateTime.now());

        return productRepository.save(product);
    }

    public Product updateProduct(Long id, Product product) {
        Product oldProduct = productRepository.findById(id).orElseThrow(
                () -> new ProductNotFoundException("Product Not Found with Id: " + id)
        );

        oldProduct.setName(product.getName());
        oldProduct.setCategory(product.getCategory());
        oldProduct.setPrice(product.getPrice());
        oldProduct.setStock(product.getStock());
        oldProduct.setBrand(product.getBrand());

        return productRepository.save(oldProduct);
    }

    public Product updateProductStock(Long id, Product product) {
        Product oldProduct = productRepository.findById(id).orElseThrow(
                () -> new ProductNotFoundException("Product Not Found with Id: " + id)
        );

        oldProduct.setStock(product.getStock());

        return productRepository.save(oldProduct);
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }
}
