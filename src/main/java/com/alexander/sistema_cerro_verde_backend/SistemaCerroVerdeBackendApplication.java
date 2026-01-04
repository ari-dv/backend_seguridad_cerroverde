package com.alexander.sistema_cerro_verde_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableAsync;



@SpringBootApplication
@EnableAsync
public class SistemaCerroVerdeBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(SistemaCerroVerdeBackendApplication.class, args);
	}

}
