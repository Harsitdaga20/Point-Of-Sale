package com.increff.pos.model.data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductRevenueData {
    private Integer quantity;
    private Double revenue;
    private String productName;
    private Double mrp;
}
