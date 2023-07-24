package com.increff.pos.model.data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InventoryReportData {
    private Integer quantity;
    private String productName;
    private Double mrp;
 }
