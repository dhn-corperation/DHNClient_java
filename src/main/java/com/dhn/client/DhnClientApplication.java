package com.dhn.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling 
public class DhnClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(DhnClientApplication.class, args);
	}

}
