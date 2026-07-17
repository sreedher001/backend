package com.mindoot.onlinestore.service;

import org.springframework.stereotype.Component;

@Component
public interface StockInterestService {

	public void registerInterest(
            Long variantId,
            Long userId,
            String email);
}
