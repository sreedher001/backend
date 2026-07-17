package com.mindoot.onlinestore.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SalesData {
    private Object period;
    private double amount;

    public SalesData(Object period, double amount) {
        this.period = period;
        this.amount = amount;
    }

    public Object getPeriod() { return period; }
    public double getAmount() { return amount; }
}

