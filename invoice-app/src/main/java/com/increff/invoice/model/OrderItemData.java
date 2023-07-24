package com.increff.invoice.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemData {
    private Integer orderId;
    private Integer orderItemId;
    private String productName;
    private Integer productId;
    private Double sellingPrice;
    private Integer quantity;
    private String barcode;
}
