package com.mindoot.onlinestore.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mindoot.onlinestore.dto.AdminStockInterestResponse;
import com.mindoot.onlinestore.service.AdminStockInterestService;


@RestController
@RequestMapping("/api/admin/stock-interest")
public class AdminStockInterestController {

	@Autowired
    private  AdminStockInterestService adminStockInterestService;

    @GetMapping("/intrested-items")
    public List<AdminStockInterestResponse> getDashboard() {
        return adminStockInterestService.getStockInterestDashboard();
    }
}