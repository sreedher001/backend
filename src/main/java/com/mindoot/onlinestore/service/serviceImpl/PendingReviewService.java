package com.mindoot.onlinestore.service.serviceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mindoot.onlinestore.dto.PendingReviewDto;
import com.mindoot.onlinestore.enums.OrderStatus;
import com.mindoot.onlinestore.model.Order;
import com.mindoot.onlinestore.model.OrderItem;
import com.mindoot.onlinestore.repository.OrderRepository;
import com.mindoot.onlinestore.repository.ProductReviewRepository;

import jakarta.transaction.Transactional;

@Service
public class PendingReviewService {

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private ProductReviewRepository reviewRepository;

	@Transactional
	public List<PendingReviewDto> getPendingReviews(Long userId) {
		List<Order> deliveredOrders = orderRepository.findByUserIdOrderByOrderDateDesc(userId).stream()
			.filter(o -> o.getStatus() == OrderStatus.DELIVERED)
			.toList();

		List<PendingReviewDto> pendingList = new ArrayList<>();

		for (Order order : deliveredOrders) {
			for (OrderItem item : order.getItems()) {
				if (item.getVariant() != null) {
					boolean alreadyReviewed = reviewRepository.existsByUserIdAndVariantId(userId, item.getVariant().getId());
					if (!alreadyReviewed) {
						String imageUrl = item.getImageUrl();
						if (imageUrl == null && item.getVariant().getImageUrl() != null) {
							imageUrl = item.getVariant().getImageUrl();
						}

						pendingList.add(PendingReviewDto.builder()
							.orderId(order.getId())
							.variantId(item.getVariant().getId())
							.productName(item.getProductName())
							.variantName(item.getVariantName())
							.imageUrl(imageUrl)
							.deliveredAt(order.getUpdatedAt())
							.build());
					}
				}
			}
		}

		return pendingList;
	}
}
