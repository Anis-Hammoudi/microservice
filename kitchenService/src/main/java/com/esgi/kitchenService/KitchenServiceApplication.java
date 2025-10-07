package com.esgi.kitchenService;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@EnableKafka
@SpringBootApplication
public class KitchenServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(KitchenServiceApplication.class, args);
	}
}
