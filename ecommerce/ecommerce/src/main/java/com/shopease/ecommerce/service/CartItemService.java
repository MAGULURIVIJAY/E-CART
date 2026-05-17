package com.shopease.ecommerce.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopease.ecommerce.model.CartItem;
import com.shopease.ecommerce.model.Product;
import com.shopease.ecommerce.model.User;
import com.shopease.ecommerce.repository.CartItemRepository;
import com.shopease.ecommerce.repository.ProductRepository;

@Service
public class CartItemService {

	@Autowired
	private CartItemRepository cartItemRepository;

	@Autowired
	private ProductRepository productRepository;

	// Add product to user's cart
	public CartItem addToCart(User user, Long productId, int quantity) {
		// 1️⃣ Fetch product from DB
		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new RuntimeException("Product not found"));

		// 2️⃣ Check if cart item for this user & product already exists
		List<CartItem> userCartItems = cartItemRepository.findByUserId(user.getId());
		CartItem cartItem = userCartItems.stream().filter(c -> c.getProduct().getId().equals(productId)).findFirst()
				// 3️⃣ If not found, create new CartItem with all 4 fields
				.orElse(new CartItem(null, user.getId(), product, 0)); // id = null, user, product, quantity=0

		// 4️⃣ Increment quantity
		cartItem.setQuantity(cartItem.getQuantity() + quantity);

		// 5️⃣ Save cart item (insert or update)
		return cartItemRepository.save(cartItem);
	}

	// Get all cart items for a user

	public List<CartItem> getCartItems(User user) {
		// Fetch all cart items that belong to this user using userId
		return cartItemRepository.findByUserId(user.getId());
	}

	// Update quantity of a product in the cart

	public CartItem updateCartItem(User user, Long productId, int quantity) {
		// Fetch all cart items for this user
		List<CartItem> userCartItems = cartItemRepository.findByUserId(user.getId());

		// Find the cart item with the given productId
		CartItem cartItem = userCartItems.stream().filter(c -> c.getProduct().getId().equals(productId)).findFirst()
				.orElseThrow(() -> new RuntimeException("Cart item not found"));

		// Set the new quantity
		cartItem.setQuantity(quantity);

		// Save updated cart item
		return cartItemRepository.save(cartItem);
	}

	// Remove a product from the user's cart

	public void removeCartItem(User user, Long productId) {
		// Delete cart item by userId and productId
		cartItemRepository.deleteByUserIdAndProductId(user.getId(), productId);
	}

}
