package com.mindoot.onlinestore.dto.expensedto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MonthlyExpenseDTO {

    private Integer year;
    private Integer month;
    private BigDecimal totalAmount;
}
