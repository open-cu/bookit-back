package ru.tbank.bookit.book_it_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BookItBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookItBackendApplication.class, args);
	}

}
