package com.example.gerenciadorbiblioteca;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@EnableMongoAuditing
public class GerenciadorbibliotecaApplication {

	public static void main(String[] args) {
		SpringApplication.run(GerenciadorbibliotecaApplication.class, args);
	}

}