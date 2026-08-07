package com.mindoot.onlinestore.dto;

import java.util.List;
import java.util.Map;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class ProductSearchRequestDto {

    private Map<String, List<String>> filters;

    private String query;

    private Double minPrice;
    private Double maxPrice;

    private Boolean inStock;

    private String sort;

    private Integer page = 0;
    private Integer size = 20;

}
