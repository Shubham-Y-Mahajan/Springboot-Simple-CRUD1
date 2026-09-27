package com.shubham.simple_crud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class SimpleCrudApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context  =SpringApplication.run(SimpleCrudApplication.class, args);

	}

}
