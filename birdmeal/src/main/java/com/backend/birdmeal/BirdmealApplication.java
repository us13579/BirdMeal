package com.backend.birdmeal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@EnableWebMvc
@EnableJpaAuditing
public class BirdmealApplication {
	public static void main(String[] args) {
		SpringApplication.run(BirdmealApplication.class, args);
	}

}