package com.sparta.oneandzerobest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class OneAndZeroBestApplication {

    public static void main(String[] args) {
        SpringApplication.run(OneAndZeroBestApplication.class, args);
    }

}
