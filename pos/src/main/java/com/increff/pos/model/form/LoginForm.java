package com.increff.pos.model.form;


import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Setter
@Getter
public class LoginForm {
	@Email
	@NotBlank
	private String email;
	@NotBlank
	@Size(min=8,max=20)
	private String password;
}
