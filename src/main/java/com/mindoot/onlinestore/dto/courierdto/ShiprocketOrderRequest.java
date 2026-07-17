package com.mindoot.onlinestore.dto.courierdto;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShiprocketOrderRequest {

    private String order_id;
    private String order_date;
    private String pickup_location;
    private String comment;

    private String billing_customer_name;
    private String billing_last_name;
    private String billing_address;
    private String billing_address_2;
    private String billing_city;
    private Integer billing_pincode;
    private String billing_state;
    private String billing_country;
    private String billing_email;
    private Long billing_phone;
    private Long billing_alternate_phone;

    private Boolean shipping_is_billing;

    private String shipping_customer_name;
    private String shipping_last_name;
    private String shipping_address;
    private String shipping_address_2;
    private String shipping_city;
    private String shipping_state;
    private String shipping_country;
    private String shipping_email;
    private String shipping_phone;
    private String shipping_pincode;

    private List<OrderItem> order_items;

    private String payment_method;

    private Integer shipping_charges;
    private Integer giftwrap_charges;
    private Integer transaction_charges;
    private Integer total_discount;
    private Integer sub_total;

    private Double length;
    private Double breadth;
    private Double height;
    private Double weight;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class OrderItem {

        private String name;
        private String sku;
        private Integer units;
        private Integer selling_price;

        private Integer discount;
        private Integer tax;
        private Integer hsn;
    }
}
