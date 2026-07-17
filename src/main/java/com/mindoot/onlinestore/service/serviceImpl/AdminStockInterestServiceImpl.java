package com.mindoot.onlinestore.service.serviceImpl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mindoot.onlinestore.dto.AdminStockInterestResponse;
import com.mindoot.onlinestore.repository.ProductVariantRepository;
import com.mindoot.onlinestore.repository.StockInterestRepository;
import com.mindoot.onlinestore.repository.projection.StockInterestSummaryProjection;
import com.mindoot.onlinestore.service.AdminStockInterestService;

@Service
public class AdminStockInterestServiceImpl implements AdminStockInterestService {

	@Autowired
	private StockInterestRepository stockInterestRepository;

	@Autowired
	private ProductVariantRepository variantRepository;

	@Override
	public List<AdminStockInterestResponse> getStockInterestDashboard() {
		List<StockInterestSummaryProjection> summary = stockInterestRepository.getStockInterestSummary();

		return summary.stream()
			.map(data -> AdminStockInterestResponse.builder()
				.variantId(data.getVariantId())
				.variantName(variantRepository.findById(data.getVariantId())
					.map(v -> v.getVariantName())
					.orElse("Unknown"))
				.waitingCount(data.getWaitingCount())
				.oldestRequest(data.getOldestRequest())
				.build())
			.collect(Collectors.toList());
	}
}
