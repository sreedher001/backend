package com.mindoot.onlinestore.service;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.mindoot.onlinestore.dto.SalesTrendResponse;

@Component
public interface DashboardService {

	SalesTrendResponse getSalesTrend(String viewMode);

	Map<String, Object> getDashboardMetrics();
}
