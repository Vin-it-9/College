package org.nexus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "org.nexus.repository")
public class CollegeInventoryApplication {

	public static void main(String[] args) {

		SpringApplication.run(CollegeInventoryApplication.class, args);

		System.out.println("Application started");
		System.out.println("http://localhost:8080/");

		
	}

}
