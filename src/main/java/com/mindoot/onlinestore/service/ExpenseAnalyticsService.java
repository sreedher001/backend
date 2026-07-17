package com.mindoot.onlinestore.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Component;

import com.mindoot.onlinestore.dto.expensedto.CategoryExpenseDTO;
import com.mindoot.onlinestore.dto.expensedto.ExpenseSummaryDTO;
import com.mindoot.onlinestore.dto.expensedto.MonthlyExpenseDTO;
import com.mindoot.onlinestore.dto.expensedto.PaymentMethodExpenseDTO;

@Component
public interface ExpenseAnalyticsService {

	public ExpenseSummaryDTO getSummary(LocalDate start, LocalDate end);
	
	public List<CategoryExpenseDTO> getCategoryAnalytics(LocalDate start, LocalDate end);

    public List<PaymentMethodExpenseDTO> getPaymentAnalytics(LocalDate start, LocalDate end);

    public List<MonthlyExpenseDTO> getMonthlyAnalytics(LocalDate start, LocalDate end);
}
