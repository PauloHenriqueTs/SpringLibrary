package com.example.library.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@Controller
@RequestMapping(path = "/signup")
public class SignupController {

	@Autowired
	private UserService userSevice;

	@GetMapping
	public Mono<String> signupForm(@ModelAttribute User user) {
		return Mono.just("signup/form");
	}

	@PostMapping
	public Mono<String> signup(@Valid User user, BindingResult result) {
		if (result.hasErrors()) {
			return signupForm(user);
		}
		return userSevice.signup(user);
	}
}
