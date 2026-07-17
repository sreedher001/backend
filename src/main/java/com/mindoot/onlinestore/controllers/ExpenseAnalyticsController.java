package com.mindoot.onlinestore.controllers;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mindoot.onlinestore.dto.expensedto.CategoryExpenseDTO;
import com.mindoot.onlinestore.dto.expensedto.ExpenseSummaryDTO;
import com.mindoot.onlinestore.dto.expensedto.MonthlyExpenseDTO;
import com.mindoot.onlinestore.dto.expensedto.PaymentMethodExpenseDTO;
import com.mindoot.onlinestore.service.ExpenseAnalyticsService;

@RestController
@RequestMapping("/api/admin/expense-analytics")
public class ExpenseAnalyticsController {

	@Autowired
    private  ExpenseAnalyticsService service;

    @GetMapping("/summary")
    public ExpenseSummaryDTO summary(
            @RequestParam("start") LocalDate start,
            @RequestParam("end") LocalDate end) {

        return service.getSummary(start, end);
    }

    @GetMapping("/category")
    public List<CategoryExpenseDTO> category(
    		@RequestParam("start") LocalDate start,
            @RequestParam("end") LocalDate end) {

        return service.getCategoryAnalytics(start, end);
    }

    @GetMapping("/payment")
    public List<PaymentMethodExpenseDTO> payment(
    		@RequestParam("start") LocalDate start,
            @RequestParam("end") LocalDate end) {

        return service.getPaymentAnalytics(start, end);
    }

    @GetMapping("/monthly")
    public List<MonthlyExpenseDTO> monthly(
    		@RequestParam("start") LocalDate start,
            @RequestParam("end") LocalDate end){

        return service.getMonthlyAnalytics(start, end);
    }
}
