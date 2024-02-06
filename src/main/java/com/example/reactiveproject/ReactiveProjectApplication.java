package com.example.reactiveproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

@SpringBootApplication
public class ReactiveProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReactiveProjectApplication.class, args);
	}

}
