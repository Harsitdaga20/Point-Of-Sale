package com.increff.pos.model.data;

import com.increff.pos.model.form.OrderItemForm;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemData extends OrderItemForm {
    private Integer orderItemId;
    private String productName;
    private Integer productId;
}
