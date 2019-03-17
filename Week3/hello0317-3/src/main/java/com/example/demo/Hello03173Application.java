package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
public class Hello03173Application {

	public static void main(String[] args) {
		SpringApplication.run(Hello03173Application.class, args);
	}

	@RequestMapping("/")
	String home() {
		return "Hello World!22";
	}

}
