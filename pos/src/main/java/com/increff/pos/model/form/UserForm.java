package com.increff.pos.model.form;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;

@Setter
@Getter
public class UserForm {
	@NotBlank
	@Email
	private String email;
	@NotBlank
	@Size(min=8,max=20)
	private String password;
	private String role;
}
