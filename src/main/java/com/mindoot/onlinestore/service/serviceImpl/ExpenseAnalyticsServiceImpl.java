package com.mindoot.onlinestore.service.serviceImpl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mindoot.onlinestore.dto.expensedto.CategoryExpenseDTO;
import com.mindoot.onlinestore.dto.expensedto.ExpenseSummaryDTO;
import com.mindoot.onlinestore.dto.expensedto.MonthlyExpenseDTO;
import com.mindoot.onlinestore.dto.expensedto.PaymentMethodExpenseDTO;
import com.mindoot.onlinestore.model.Invoice;
import com.mindoot.onlinestore.repository.InvoiceRepository;
import com.mindoot.onlinestore.service.ExpenseAnalyticsService;

@Service
public class ExpenseAnalyticsServiceImpl implements ExpenseAnalyticsService {

	@Autowired
    private InvoiceRepository invoiceRepository;

    public ExpenseSummaryDTO getSummary(LocalDate start, LocalDate end) {

        List<Invoice> invoices =
                invoiceRepository.findByInvoiceDateBetween(start, end);

        BigDecimal total = invoices.stream()
                .map(Invoice::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Long count = (long) invoices.size();

        BigDecimal average = count == 0
                ? BigDecimal.ZERO
                : total.divide(
                        BigDecimal.valueOf(count),
                        2, // scale (2 decimal places)
                        RoundingMode.HALF_UP
                );

        BigDecimal highest = invoices.stream()
                .map(Invoice::getAmount)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        BigDecimal lowest = invoices.stream()
                .map(Invoice::getAmount)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        return new ExpenseSummaryDTO(
                total, count, average, highest, lowest
        );
    }

    public List<CategoryExpenseDTO> getCategoryAnalytics(LocalDate start, LocalDate end) {
        return invoiceRepository.getCategoryWiseExpense(start, end);
    }

    public List<PaymentMethodExpenseDTO> getPaymentAnalytics(LocalDate start, LocalDate end) {
        return invoiceRepository.getPaymentMethodWiseExpense(start, end);
    }

    public List<MonthlyExpenseDTO> getMonthlyAnalytics(LocalDate start, LocalDate end) {
        return invoiceRepository.getMonthlyExpense(start, end);
    }
}
