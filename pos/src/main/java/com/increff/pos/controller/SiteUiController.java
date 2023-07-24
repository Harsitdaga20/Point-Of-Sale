package com.increff.pos.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Objects;

@Controller
public class SiteUiController extends AbstractUiController {

    @RequestMapping(value = "")
    public ModelAndView index() {
        return mav("index.html");
    }

    @RequestMapping(value = "/site/login")
    public ModelAndView login() {
        return mav("login.html");
    }

    @RequestMapping(value = "/site/signup")
    public ModelAndView signup() {
        return mav("signup.html");
    }


}
