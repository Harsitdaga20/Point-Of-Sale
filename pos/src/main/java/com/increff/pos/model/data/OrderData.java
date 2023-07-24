package com.increff.pos.model.data;

import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Setter
@Getter
public class OrderData{
    private Integer orderId;
    private ZonedDateTime orderCreatedTime;
    private ZonedDateTime orderInvoicedTime;
    private String status;
    private String orderCode;
}
