package com.opencu.bookit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = "com.opencu.bookit")
public class BookItBackendApplication {
	public static void main(String[] args) {
		SpringApplication.run(BookItBackendApplication.class, args);
	}
}
