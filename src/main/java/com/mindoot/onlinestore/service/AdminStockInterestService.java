package com.mindoot.onlinestore.service;

import java.util.List;

import org.springframework.stereotype.Component;

import com.mindoot.onlinestore.dto.AdminStockInterestResponse;

@Component
public interface AdminStockInterestService {

    List<AdminStockInterestResponse> getStockInterestDashboard();
}
