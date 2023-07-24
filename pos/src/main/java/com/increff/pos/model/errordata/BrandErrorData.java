package com.increff.pos.model.errordata;

import lombok.Getter;
import lombok.Setter;
import com.increff.pos.model.form.BrandForm;

@Setter
@Getter
public class BrandErrorData extends BrandForm {
    private String message;
    private Integer lineNumber;
}
