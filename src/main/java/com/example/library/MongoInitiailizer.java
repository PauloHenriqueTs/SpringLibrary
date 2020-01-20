
package com.example.library;

import com.example.library.image.Image;

import com.example.library.user.User;
import com.example.library.user.UserRepository;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.boot.CommandLineRunner;

@Component
class MongoInitiailizer implements SmartInitializingSingleton {

    private final UserRepository users;
    public static String UPLOAD_ROOT = "upload-dir";

    MongoInitiailizer(UserRepository users) {

        this.users = users;
    }

    @Override
    public void afterSingletonsInstantiated() {
        // sha256 w/ salt encoded "password"
        String passsword = "73ac8218b92f7494366bf3a03c0c2ee2095d0c03a29cb34c95da327c7aa17173248af74d46ba2d4c";

        User rob = new User(1L, "rob@example.com", passsword, "Rob", "Winch");
        User joe = new User(100L, "joe@example.com", passsword, "Joe", "Grandja");

        this.users.save(rob).block();
        this.users.save(joe).block();

        this.users.findAll().doOnNext(user -> user.setPassword("{sha256}" + user.getPassword()))
                .flatMap(this.users::save).collectList().block();
    }

    @Bean
    CommandLineRunner init(MongoOperations operations) {
        return args -> {
            // tag::log[]
            operations.dropCollection(Image.class);
            operations.createCollection(Image.class);

            operations.insert(new Image("1", "learning-spring-boot-cover.jpg"));
            operations.insert(new Image("2", "learning-spring-boot-2nd-edition-cover.jpg"));
            operations.insert(new Image("3", "bazinga.png"));

            operations.findAll(Image.class).forEach(image -> {
                System.out.println(image.toString());
            });
            // end::log[]
        };
    }

}