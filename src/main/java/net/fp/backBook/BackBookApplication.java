package net.fp.backBook;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories
public class BackBookApplication {


	public static void main(String[] args) {
		SpringApplication.run(BackBookApplication.class, args);
	}

}

