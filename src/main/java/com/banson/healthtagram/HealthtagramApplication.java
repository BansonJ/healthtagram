package com.banson.healthtagram;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class HealthtagramApplication {

	public static void main(String[] args) {
		SpringApplication.run(HealthtagramApplication.class, args);
	}

}
