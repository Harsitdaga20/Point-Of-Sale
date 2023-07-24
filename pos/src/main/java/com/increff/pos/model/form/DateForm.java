package com.increff.pos.model.form;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

@Getter
@Setter
public class DateForm {
    @NotNull
    private ZonedDateTime start;
    @NotNull
    private ZonedDateTime end;
}
