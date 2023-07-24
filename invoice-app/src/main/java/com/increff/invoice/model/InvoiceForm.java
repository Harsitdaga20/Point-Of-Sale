package com.increff.invoice.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class InvoiceForm {
    private String orderCode;
    private String orderInvoicedTime;
    private List<OrderItemData> orderItemDataList;
}
