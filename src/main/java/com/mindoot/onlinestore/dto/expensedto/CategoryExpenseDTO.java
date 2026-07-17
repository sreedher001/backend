package com.mindoot.onlinestore.dto.expensedto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CategoryExpenseDTO {

    private String category;
    private BigDecimal totalAmount;
}
