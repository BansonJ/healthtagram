package com.banson.healthtagram;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.banson.healthtagram")
public class HealthtagramApplication {

	public static void main(String[] args) {
		SpringApplication.run(HealthtagramApplication.class, args);
	}
}
