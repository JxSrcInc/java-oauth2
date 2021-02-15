package jxsource.oauth2.proxy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import jxsource.oauth2.jwt.util.ClientJwtUtil;

@SpringBootApplication
public class AuthProxy {

	public static void main(String... args) {
		SpringApplication.run(AuthProxy.class, args);
	}

	@Bean
	public ClientJwtUtil createClientJwtUtil() {
		return new ClientJwtUtil();
	}

}
