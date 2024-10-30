package com.whh.findmusechatting;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@EnableReactiveMongoRepositories
@SpringBootApplication
public class FindMuseChattingApplication {
	public static void main(String[] args) {
		SpringApplication.run(FindMuseChattingApplication.class, args);
	}
}
