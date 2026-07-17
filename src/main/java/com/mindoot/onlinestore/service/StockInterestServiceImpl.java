package com.mindoot.onlinestore.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mindoot.onlinestore.model.StockInterest;
import com.mindoot.onlinestore.repository.StockInterestRepository;

@Service
public class StockInterestServiceImpl implements StockInterestService {

	@Autowired
	private StockInterestRepository repository;

	public void registerInterest(Long variantId, Long userId, String email) {

		if (userId != null) {
			boolean exists = repository.existsByVariantIdAndUserIdAndNotifiedFalse(variantId, userId);
			if (exists) return;
		} else {
			boolean exists = repository.existsByVariantIdAndEmailAndNotifiedFalse(variantId, email);
			if (exists) return;
		}

		StockInterest interest = new StockInterest();
		interest.setVariantId(variantId);
		interest.setUserId(userId);
		interest.setEmail(email);

		repository.save(interest);
	}

}
