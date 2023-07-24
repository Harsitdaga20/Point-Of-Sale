package com.increff.pos.model.form;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;

@Setter
@Getter
public class InventoryForm {
    @NotBlank
    @Size(min=1,max=8)
    private String barcode;
    @NotNull
    @Positive
    @Min(1)
    @Max(100000000)
    private Integer quantity;
}
