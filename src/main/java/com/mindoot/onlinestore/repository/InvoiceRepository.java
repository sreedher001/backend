package com.mindoot.onlinestore.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mindoot.onlinestore.dto.expensedto.CategoryExpenseDTO;
import com.mindoot.onlinestore.dto.expensedto.MonthlyExpenseDTO;
import com.mindoot.onlinestore.dto.expensedto.PaymentMethodExpenseDTO;
import com.mindoot.onlinestore.model.Invoice;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    
	Page<Invoice> findByCreatedAtGreaterThanEqualAndCreatedAtLessThan(LocalDateTime start, LocalDateTime end, Pageable pageable);
	
	List<Invoice> findByInvoiceDateBetween(LocalDate start, LocalDate end);

    @Query("""
        SELECT new com.mindoot.onlinestore.dto.expensedto.CategoryExpenseDTO(
            i.category,
            SUM(i.amount)
        )
        FROM Invoice i
        WHERE i.invoiceDate BETWEEN :start AND :end
        GROUP BY i.category
    """)
    List<CategoryExpenseDTO> getCategoryWiseExpense(
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );

    @Query("""
        SELECT new com.mindoot.onlinestore.dto.expensedto.PaymentMethodExpenseDTO(
            i.paymentMethod,
            SUM(i.amount)
        )
        FROM Invoice i
        WHERE i.invoiceDate BETWEEN :start AND :end
        GROUP BY i.paymentMethod
    """)
    List<PaymentMethodExpenseDTO> getPaymentMethodWiseExpense(
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );

    @Query("""
        SELECT new com.mindoot.onlinestore.dto.expensedto.MonthlyExpenseDTO(
            YEAR(i.invoiceDate),
            MONTH(i.invoiceDate),
            SUM(i.amount)
        )
        FROM Invoice i
        WHERE i.invoiceDate BETWEEN :start AND :end
        GROUP BY YEAR(i.invoiceDate), MONTH(i.invoiceDate)
        ORDER BY YEAR(i.invoiceDate), MONTH(i.invoiceDate)
    """)
    List<MonthlyExpenseDTO> getMonthlyExpense(
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );

}
