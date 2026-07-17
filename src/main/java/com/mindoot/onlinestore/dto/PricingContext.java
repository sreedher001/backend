package com.mindoot.onlinestore.dto;

import java.util.List;

import com.mindoot.onlinestore.model.CartItem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PricingContext {

    private double subtotal;
    private double shippingFee;
    private int totalQuantity;
    private List<CartItem> items;

}