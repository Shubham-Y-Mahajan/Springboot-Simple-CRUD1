package com.shubham.simple_crud1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class SimpleCrud1Application {

	public static void main(String[] args) {
		ConfigurableApplicationContext context  =SpringApplication.run(SimpleCrud1Application.class, args);
		// The return type of run method is Configurable ApplicationContext
		//this is when JVM will initialize your spring container
		Alien a = context.getBean(Alien.class);

		System.out.println(a);

	}

}
