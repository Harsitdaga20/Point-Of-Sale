package com.increff.pos.model.form;


import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;

@Getter
@Setter
public class ProductForm {
    @NotBlank
    @Size(min=1,max=30)
    @NotNull
    private String productName;
    @Positive
    @NotNull
    @Min(0)
    @Max(100000000)
    private Double mrp;
    @NotEmpty
    @NotBlank
    @Size(min=1,max=30)
    private String brandName;
    @NotBlank
    @Size(min=1,max=30)
    @NotNull
    private String brandCategory;
}
