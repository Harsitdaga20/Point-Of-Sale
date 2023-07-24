package com.increff.pos.model.form;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;

@Getter
@Setter
public class OrderItemForm {
    @NotNull
    private Integer orderId;
    @NotNull
    @Positive
    @Min(1)
    @Max(100000000)
    private Integer quantity;
    @NotBlank
    @Size(min=1,max=8)
    private String barcode;
    @NotNull
    @Min(0)
    @Max(100000000)
    private Double sellingPrice;
}
