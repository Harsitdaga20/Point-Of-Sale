package com.increff.pos.model.form;

import lombok.Getter;
import lombok.Setter;
import com.increff.pos.model.data.OrderItemData;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
public class InvoiceForm {
    @NotBlank
    @Size(min=1,max=20)
    private String orderCode;
    @NotBlank
    private String orderInvoicedTime;
    private List<OrderItemData> orderItemDataList;
}
