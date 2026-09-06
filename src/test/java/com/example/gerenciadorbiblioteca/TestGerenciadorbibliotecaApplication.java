package com.example.gerenciadorbiblioteca;

import org.springframework.boot.SpringApplication;

public class TestGerenciadorbibliotecaApplication {

	public static void main(String[] args) {
		SpringApplication.from(GerenciadorbibliotecaApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
