package com.mindoot.onlinestore.utility;

import com.mindoot.onlinestore.model.Product;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScoredProduct {
    private Product product;
    private int score; // number of matched filters
    private boolean isExactMatch;

    public ScoredProduct(Product product) {
        this.product = product;
        this.score = 0;
        this.isExactMatch = false;
    }

    public void incrementScore() {
        this.score++;
    }

    public void markExactMatch() {
        this.isExactMatch = true;
        this.score = 999; // push exact match to top
    }

}

