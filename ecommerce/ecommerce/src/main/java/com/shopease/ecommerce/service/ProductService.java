package com.shopease.ecommerce.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopease.ecommerce.model.Product;
import com.shopease.ecommerce.repository.ProductRepository;

@Service
public class ProductService {

	@Autowired
	private ProductRepository productRepository;

	// 🧠 Fetch all products
	public List<Product> getAllProducts() {
		return productRepository.findAll();
	}

	// ➕ Create a product
	public Product addProduct(Product product) {
		return productRepository.save(product);
	}

	// ✏️ Update a product
	public Product updateProduct(Long id, Product updatedProduct) {
		return productRepository.findById(id).map(product -> {
			product.setName(updatedProduct.getName());
			product.setDescription(updatedProduct.getDescription());
			product.setPrice(updatedProduct.getPrice());
			product.setStock(updatedProduct.getStock());
			product.setImageUrl(updatedProduct.getImageUrl());
			return productRepository.save(product);
		}).orElseThrow(() -> new RuntimeException("Product not found"));
	}

	// ❌ Delete a product
	public void deleteProduct(Long id) {
		productRepository.deleteById(id);
	}
}
