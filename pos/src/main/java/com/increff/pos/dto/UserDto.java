package com.increff.pos.dto;


import com.increff.pos.model.data.InfoData;
import com.increff.pos.model.form.LoginForm;
import com.increff.pos.pojo.UserPojo;
import com.increff.pos.service.UserService;
import com.increff.pos.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Objects;

@Service
public class UserDto {

    @Autowired
    private UserService userService;
    @Autowired
    private InfoData infoData;

    @Value("${supervisor.email}")
    private String supervisorEmail;

    public ModelAndView login(HttpServletRequest request, LoginForm loginForm) {
        NormalizeUtil.normalizeLoginForm(loginForm);

        UserPojo userPojo = userService.getCheckUserByEmail(loginForm.getEmail());
        if(Objects.isNull(userPojo)){
            infoData.setMessage("User not registered. Kindly signup.");
            return new ModelAndView("redirect:/site/login");
        }
        boolean validatePass = false;
        try {
            validatePass = PasswordUtil.validatePassword(loginForm.getPassword(), userPojo.getPassword());
        } catch (Exception e) {
            infoData.setMessage("Error validating password");
            return new ModelAndView("redirect:/site/login");
        }

        if (Objects.isNull(userPojo) || !validatePass) {
            infoData.setMessage("Invalid user credentials");
            return new ModelAndView("redirect:/site/login");
        }

        infoData.setMessage("");
        infoData.setEmail(loginForm.getEmail());
        infoData.setRole(userPojo.getRole());
        Authentication authentication = convertUserPojoToAuthenticate(userPojo);
        HttpSession session = request.getSession(true);
        SecurityUtil.createContext(session);
        SecurityUtil.setAuthentication(authentication);
        return new ModelAndView("redirect:/ui/home");
    }

    public ModelAndView signup(HttpServletRequest req, LoginForm loginForm) {
        NormalizeUtil.normalizeLoginForm(loginForm);

        try {
            ValidateFormUtil.validateForm(loginForm);
        } catch (Exception e) {
            infoData.setMessage(e.getMessage());
            return new ModelAndView("redirect:/site/signup");
        }
        if (Objects.nonNull(userService.getCheckUserByEmail(loginForm.getEmail()))) {
            infoData.setMessage("Email Already Exist");
            return new ModelAndView("redirect:/site/signup");
        }
        try {
            loginForm.setPassword(PasswordUtil.generateSecurePassword(loginForm.getPassword()));
        } catch (Exception e) {
            infoData.setMessage("Password Hashing failed");
            return new ModelAndView("redirect:/site/signup");
        }
        userService.getCheckUser(ConvertUtil.convertLoginFormToUserPojo(loginForm));
        userService.addUser(ConvertUtil.convertLoginFormToUserPojo(loginForm));
        infoData.setMessage("");
        return login(req, loginForm);
    }

    private Authentication convertUserPojoToAuthenticate(UserPojo userPojo) {
        UserPrincipal principal = new UserPrincipal();
        principal.setEmail(userPojo.getEmail());
        principal.setId(userPojo.getId());
        String[] superVisorEmails = supervisorEmail.split(",");
        String role = "operator";
        for (String superVisorEmail : superVisorEmails) {
            if (superVisorEmail.trim().equalsIgnoreCase(userPojo.getEmail().trim())) {
                role = "supervisor";
                break;
            }
        }
        principal.setRole(role);
        ArrayList<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(userPojo.getRole()));
        return new UsernamePasswordAuthenticationToken(principal, null,
                authorities);
    }

}
