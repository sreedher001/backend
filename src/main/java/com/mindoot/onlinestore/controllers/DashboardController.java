package com.mindoot.onlinestore.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mindoot.onlinestore.dto.SalesTrendResponse;
import com.mindoot.onlinestore.service.DashboardService;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

	@Autowired
	private DashboardService dashboardService;

	@GetMapping("/sales-trend")
	public ResponseEntity<SalesTrendResponse> getSalesTrend(@RequestParam("view") String view) {
		SalesTrendResponse response = dashboardService.getSalesTrend(view);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/metrics")
	public ResponseEntity<Map<String, Object>> getDashboardMetrics() {
		return ResponseEntity.ok(dashboardService.getDashboardMetrics());
	}
}
