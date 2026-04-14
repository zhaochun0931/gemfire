package com.example.hello_world;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.gemfire.config.annotation.EnableCachingDefinedRegions;


@SpringBootApplication
@EnableCaching
@EnableCachingDefinedRegions
public class HelloWorldApplication {

	public static void main(String[] args) {
		SpringApplication.run(HelloWorldApplication.class, args);
	}

}