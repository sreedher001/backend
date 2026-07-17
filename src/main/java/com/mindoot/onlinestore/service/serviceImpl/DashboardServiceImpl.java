package com.mindoot.onlinestore.service.serviceImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mindoot.onlinestore.dto.SalesTrendResponse;
import com.mindoot.onlinestore.model.Order;
import com.mindoot.onlinestore.repository.OrderRepository;
import com.mindoot.onlinestore.repository.ProductRepository;
import com.mindoot.onlinestore.service.DashboardService;

@Service
public class DashboardServiceImpl implements DashboardService {

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private ProductRepository productRepository;

	@Override
	public SalesTrendResponse getSalesTrend(String viewMode) {
		LocalDate today = LocalDate.now();

		List<Order> orders;
		Map<String, Double> groupedSales = new LinkedHashMap<>();
		DateTimeFormatter formatter;

		switch (viewMode.toUpperCase()) {
			case "DAILY":
				formatter = DateTimeFormatter.ofPattern("dd MMM");
				LocalDate startDate = today.minusDays(29);
				LocalDateTime startDateTime = startDate.atStartOfDay();
				orders = orderRepository.findOrdersSince(startDateTime);
				groupedSales = orders.stream()
					.collect(Collectors.groupingBy(
						o -> o.getOrderDate().format(formatter),
						LinkedHashMap::new,
						Collectors.summingDouble(Order::getTotalAmount)
					));
				break;

			case "MONTHLY":
				formatter = DateTimeFormatter.ofPattern("MMM yyyy");
				LocalDateTime monthlyStart = today
					.minusMonths(11)
					.withDayOfMonth(1)
					.atStartOfDay();
				orders = orderRepository.findOrdersSince(monthlyStart);
				groupedSales = orders.stream()
					.collect(Collectors.groupingBy(
						o -> o.getOrderDate().withDayOfMonth(1).format(formatter),
						LinkedHashMap::new,
						Collectors.summingDouble(Order::getTotalAmount)
					));
				break;

			case "YEARLY":
				formatter = DateTimeFormatter.ofPattern("yyyy");
				LocalDateTime yearlyStart = today.minusYears(4).withDayOfYear(1).atStartOfDay();
				orders = orderRepository.findOrdersSince(yearlyStart);
				groupedSales = orders.stream()
					.collect(Collectors.groupingBy(
						o -> String.valueOf(o.getOrderDate().getYear()),
						LinkedHashMap::new,
						Collectors.summingDouble(Order::getTotalAmount)
					));
				break;

			default:
				throw new IllegalArgumentException("Invalid view mode: " + viewMode);
		}

		List<String> labels = new ArrayList<>(groupedSales.keySet());
		List<Double> values = new ArrayList<>(groupedSales.values());
		double growth = calculateGrowth(values);
		String bestPeriod = getBestPeriod(groupedSales);

		SalesTrendResponse response = new SalesTrendResponse();
		response.setLabels(labels);
		response.setValues(values);
		response.setGrowth(growth);
		response.setBestPeriod(bestPeriod);

		return response;
	}

	@Override
	public Map<String, Object> getDashboardMetrics() {
		Map<String, Object> metrics = new HashMap<>();

		// Order counts
		long totalOrders = orderRepository.countAllActiveOrders();
		long retailOrders = orderRepository.countRetailOrders();
		long wholesaleOrders = orderRepository.countWholesaleOrders();

		// Revenue
		Double totalRevenue = orderRepository.sumAllActiveSales();
		Double retailRevenue = orderRepository.sumRetailSales();
		Double wholesaleRevenue = orderRepository.sumWholesaleSales();

		metrics.put("totalOrders", totalOrders);
		metrics.put("retailOrders", retailOrders);
		metrics.put("wholesaleOrders", wholesaleOrders);
		metrics.put("totalRevenue", totalRevenue != null ? totalRevenue : 0.0);
		metrics.put("retailRevenue", retailRevenue != null ? retailRevenue : 0.0);
		metrics.put("wholesaleRevenue", wholesaleRevenue != null ? wholesaleRevenue : 0.0);
		metrics.put("totalProducts", productRepository.count());

		return metrics;
	}

	private double calculateGrowth(List<Double> values) {
		if (values.size() < 2) return 0;
		int mid = values.size() / 2;
		double oldSum = values.subList(0, mid).stream().mapToDouble(Double::doubleValue).sum();
		double newSum = values.subList(mid, values.size()).stream().mapToDouble(Double::doubleValue).sum();
		return oldSum == 0 ? 0 : ((newSum - oldSum) / oldSum) * 100;
	}

	private String getBestPeriod(Map<String, Double> grouped) {
		return grouped.entrySet().stream()
			.max(Comparator.comparingDouble(Map.Entry::getValue))
			.map(Map.Entry::getKey)
			.orElse("-");
	}
}
