package com.shubham.simple_crud1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class SimpleCrud1Application {

	public static void main(String[] args) {
		ConfigurableApplicationContext context  =SpringApplication.run(SimpleCrud1Application.class, args);
		//this is when JVM will initialize your spring container
		// The return type of run method is Configurable ApplicationContext
		// This is the Reference Type of the Spring Context

		Alien a = context.getBean(Alien.class);
		// even if you dont write .getBean , spring will still create a bean for all spring managed objects
		//  as spring by default follows a singleton pattern
		// .getBean simply fetches that object from the spring container inside the jvm, but object is already present in spring container
		System.out.println(a);

		Alien a2 = context.getBean(Alien.class);
		System.out.println(a2);

	}

}
