package com.increff.pos.model.errordata;

import lombok.Getter;
import lombok.Setter;
import com.increff.pos.model.form.InventoryForm;

@Getter
@Setter
public class InventoryErrorData extends InventoryForm {
    private String message;
    private Integer lineNumber;
}
