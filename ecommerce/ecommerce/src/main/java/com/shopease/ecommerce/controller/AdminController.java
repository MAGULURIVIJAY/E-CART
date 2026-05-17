package com.shopease.ecommerce.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shopease.ecommerce.model.Order;
import com.shopease.ecommerce.model.Product;
import com.shopease.ecommerce.service.OrderService;
import com.shopease.ecommerce.service.ProductService;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:3000")
public class AdminController {

	@Autowired
	private ProductService productService;

	@Autowired
	private OrderService orderService;

	// ---------------------------
	// PRODUCT MANAGEMENT (ADMIN)
	// ---------------------------

	@PostMapping("/product")
	public Product addProduct(@RequestBody Product product) {
		return productService.addProduct(product);
	}

	@PutMapping("/product/{id}")
	public Product updateProduct(@PathVariable Long id, @RequestBody Product product) {
		return productService.updateProduct(id, product);
	}

	@DeleteMapping("/product/{id}")
	public String deleteProduct(@PathVariable Long id) {
		productService.deleteProduct(id);
		return "Product deleted successfully!";
	}

	@GetMapping("/products")
	public List<Product> getAllProducts() {
		return productService.getAllProducts();
	}

	// ORDER MANAGEMENT (ADMIN)

	@GetMapping("/orders")
	public List<Order> getAllOrders() {
		return orderService.getAllOrdersForAdmin();
	}

	@GetMapping("/orders/{orderId}")
	public Order getOrderDetails(@PathVariable Long orderId) {
		return orderService.getOrderById(orderId);
	}

}
