package com.example.library.security;

import com.example.library.user.User;

import org.springframework.security.web.reactive.result.view.CsrfRequestDataValueProcessor;
import org.springframework.security.web.server.csrf.CsrfToken;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@ControllerAdvice
public class SecurityControllerAdvice {
    @ModelAttribute("currentUser")
    User currentUser(@CurrentUser User currentUser) {
        return currentUser;
    }
}
