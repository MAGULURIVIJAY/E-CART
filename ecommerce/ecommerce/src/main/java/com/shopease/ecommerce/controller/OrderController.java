package com.shopease.ecommerce.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shopease.ecommerce.model.Order;
import com.shopease.ecommerce.model.User;
import com.shopease.ecommerce.security.JwtUtil;
import com.shopease.ecommerce.service.OrderService;
import com.shopease.ecommerce.service.UserService;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "http://localhost:3000")
public class OrderController {


	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private UserService userService;

	@Autowired
	private OrderService orderService;

	private User getUserFromToken(String authHeader) {
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			throw new RuntimeException("Missing or invalid Authorization header");
		}
		String token = authHeader.substring(7);
		String email = jwtUtil.extractEmail(token);
		return userService.getUserByEmail(email);
	}

	@GetMapping
	public List<Order> getOrders(@RequestHeader("Authorization") String authHeader) {
		User user = getUserFromToken(authHeader);
		return orderService.getOrdersByUser(user);
	}

	// Place an order (convert user's cart into an order)
	@PostMapping("/place")
	public Order placeOrder(@RequestHeader("Authorization") String authHeader) {
		User user = getUserFromToken(authHeader);
		return orderService.placeOrder(user);
	}

	// PUT - Update order status
	@PutMapping("/{orderId}")
	public Order updateOrder(@RequestHeader("Authorization") String authHeader, @PathVariable Long orderId,
			@RequestParam String status) {
		User user = getUserFromToken(authHeader);
		return orderService.updateOrder(user, orderId, status);
	}

	// DELETE - Delete order
	@DeleteMapping("/{orderId}")
	public String deleteOrder(@RequestHeader("Authorization") String authHeader, @PathVariable Long orderId) {
		User user = getUserFromToken(authHeader);
		orderService.deleteOrder(user, orderId);
		return "Order deleted successfully";
	}

}
