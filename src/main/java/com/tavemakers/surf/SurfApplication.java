package com.tavemakers.surf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class SurfApplication {
    public static void main(String[] args) {
        SpringApplication.run(SurfApplication.class, args);
    }
}
