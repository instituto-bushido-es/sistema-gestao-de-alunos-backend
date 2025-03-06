package br.org.institutobushido;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableCaching
@EnableTransactionManagement
public class InstitutobushidoApplication {
	public static void main(String[] args) {
		SpringApplication.run(InstitutobushidoApplication.class, args);
	}
}