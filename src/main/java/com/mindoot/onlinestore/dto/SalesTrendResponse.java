package com.mindoot.onlinestore.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class SalesTrendResponse {
    private List<String> labels;
    private List<Double> values;
    private double growth;
    private String bestPeriod;

}

