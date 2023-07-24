package com.increff.pos.model.form;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
public class DateWithBrandCategoryForm extends DateForm{
    @NotBlank
    @Size(min=1,max=30)
    private String brand;
    @NotBlank
    @Size(min=1,max=30)
    private String category;
}
