package ru.tbank.bookit.book_it_backend;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.nio.file.Files;
import java.nio.file.Paths;

@SpringBootApplication
@EnableScheduling
public class BookItBackendApplication {
	public static void main(String[] args) {
		if (Files.exists(Paths.get(".env"))) {
			Dotenv dotenv = Dotenv.configure().load();
			dotenv.entries().forEach(e -> System.setProperty(e.getKey(), e.getValue()));
		}

		SpringApplication.run(BookItBackendApplication.class, args);
	}
}
