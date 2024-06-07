package com.example.helloworld;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalTime;






import org.apache.geode.cache.GemFireCache;
import org.springframework.beans.factory.annotation.Autowired;



@SpringBootApplication

@RestController

public class HelloworldApplication {


	 @Autowired
    private GemFireCache gemfireCache;




	public static void main(String[] args) {
		SpringApplication.run(HelloworldApplication.class, args);
	}

	    @GetMapping("/hello")
	        public String getData() {
        return "Data from GemFire: " + gemfireCache.getRegion("exampleRegion").get("exampleKey");
    }


}
