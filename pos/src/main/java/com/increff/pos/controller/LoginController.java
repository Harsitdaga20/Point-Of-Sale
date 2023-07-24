package com.increff.pos.controller;

import com.increff.pos.dto.UserDto;
import com.increff.pos.model.form.LoginForm;
import com.increff.pos.service.ApiException;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class LoginController {

    @Autowired
    private UserDto userDto;

    @ApiOperation(value = "Logs in a user")
    @RequestMapping(path = "/session/login", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ModelAndView login(HttpServletRequest req, LoginForm loginForm) throws ApiException {
        return userDto.login(req, loginForm);
    }

    @RequestMapping(path = "/session/logout", method = RequestMethod.GET)
    public ModelAndView logout(HttpServletRequest request, HttpServletResponse response) {
        request.getSession().invalidate();
        return new ModelAndView("redirect:/");
    }

    @RequestMapping(path = "/session/signup", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ModelAndView signup(HttpServletRequest req, LoginForm loginForm) throws ApiException {
        return userDto.signup(req, loginForm);
    }

}
