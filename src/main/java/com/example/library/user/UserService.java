package com.example.library.user;

import java.security.SecureRandom;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class UserService {

    @Autowired
    private UserRepository users;

    @Autowired
    private PasswordEncoder encoder;

    private SecureRandom random = new SecureRandom();

    public Mono<String> signup(@Valid User user) {

        return Mono.just(user).doOnNext(u -> u.setId(this.random.nextLong())).subscribeOn(Schedulers.parallel())
                .doOnNext(u -> u.setPassword(this.encoder.encode(u.getPassword()))).flatMap(this.users::save)
                .then(Mono.just("redirect:/"));
    }
}