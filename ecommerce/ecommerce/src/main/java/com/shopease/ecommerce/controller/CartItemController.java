package com.shopease.ecommerce.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shopease.ecommerce.model.CartItem;
import com.shopease.ecommerce.model.User;
import com.shopease.ecommerce.security.JwtUtil;
import com.shopease.ecommerce.service.CartItemService;
import com.shopease.ecommerce.service.UserService;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "http://localhost:3000")
public class CartItemController {

	

	@Autowired
	private CartItemService cartItemService;

	@Autowired
	private JwtUtil jwtUtil;
	@Autowired
	private UserService userService;

	private User getUserFromToken(String authHeader) {
		// authHeader: "Bearer <token>"
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			throw new RuntimeException("Missing or invalid Authorization header");
		}

		String token = authHeader.substring(7); // Remove "Bearer " prefix
		String email = jwtUtil.extractEmail(token); // Extract email from token
		return userService.getUserByEmail(email); // Fetch User object from DB
	}

	@GetMapping
	public List<CartItem> getCartItems(@RequestHeader("Authorization") String authHeader) {
		User user = getUserFromToken(authHeader);
		return cartItemService.getCartItems(user);
	}

	// add a product to cart
	@PostMapping
	public CartItem addToCart(@RequestHeader("Authorization") String authHeader, @RequestParam Long productId,
			@RequestParam int quantity) {

		User user = getUserFromToken(authHeader);
		return cartItemService.addToCart(user, productId, quantity);
	}

	// PUT /api/cart → update quantity of a product in cart
	@PutMapping
	public CartItem updateCartItem(@RequestHeader("Authorization") String authHeader, @RequestParam Long productId,
			@RequestParam int quantity) {

		User user = getUserFromToken(authHeader);
		return cartItemService.updateCartItem(user, productId, quantity);
	}

	// DELETE /api/cart → remove a product from cart
	@DeleteMapping
	public String removeCartItem(@RequestHeader("Authorization") String authHeader, @RequestParam Long productId) {

		User user = getUserFromToken(authHeader);
		cartItemService.removeCartItem(user, productId);
		return "Cart item removed successfully";
	}

}
