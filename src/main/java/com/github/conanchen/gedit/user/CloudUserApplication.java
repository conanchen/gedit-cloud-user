package com.github.conanchen.gedit.user;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
@SpringBootApplication
public class CloudUserApplication {

	public static void main(String[] args) {
		SpringApplication.run(CloudUserApplication.class, args);
	}

}
