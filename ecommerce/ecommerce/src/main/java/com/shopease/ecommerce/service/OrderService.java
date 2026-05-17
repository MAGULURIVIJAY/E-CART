package com.shopease.ecommerce.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopease.ecommerce.model.CartItem;
import com.shopease.ecommerce.model.Order;
import com.shopease.ecommerce.model.User;
import com.shopease.ecommerce.repository.CartItemRepository;
import com.shopease.ecommerce.repository.OrderRepository;

@Service
public class OrderService {

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private CartItemRepository cartItemRepository;

	// Get all orders for a user
	public List<Order> getOrdersByUser(User user) {
		return orderRepository.findByUserId(user.getId());
	}

	// place an order for the logged-in user
	public Order placeOrder(User user) {
		List<CartItem> cartItems = cartItemRepository.findByUserId(user.getId());

		if (cartItems.isEmpty()) {
			throw new RuntimeException("Cart is empty! Cannot place order.");
		}

		// Calculate total amount
		double totalAmount = cartItems.stream().mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
				.sum();

		// Create order
		Order order = new Order();
		order.setUserId(user.getId());
		order.setOrderDate(LocalDateTime.now());
		order.setStatus("Pending");
		order.setTotalAmount(totalAmount);
		order.setItems(cartItems);

		Order savedOrder = orderRepository.save(order);

		// Clear cart
		cartItemRepository.deleteAll(cartItems);

		return savedOrder;
	}

	// Update order status (user can update only own orders)
	public Order updateOrder(User user, Long orderId, String newStatus) {
		Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));

		if (!order.getUserId().equals(user.getId())) {
			throw new RuntimeException("Cannot update someone else's order");
		}

		order.setStatus(newStatus);
		return orderRepository.save(order);
	}

	// Delete an order (user can delete only own orders)
	public void deleteOrder(User user, Long orderId) {
		Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));

		if (!order.getUserId().equals(user.getId())) {
			throw new RuntimeException("Cannot delete someone else's order");
		}

		orderRepository.delete(order);
	}
	
	// ADMIN: Get all orders
	public List<Order> getAllOrdersForAdmin() {
	    return orderRepository.findAll();
	}

	// ADMIN: Get single order details
	public Order getOrderById(Long orderId) {
	    return orderRepository.findById(orderId)
	            .orElseThrow(() -> new RuntimeException("Order not found"));
	}
}
