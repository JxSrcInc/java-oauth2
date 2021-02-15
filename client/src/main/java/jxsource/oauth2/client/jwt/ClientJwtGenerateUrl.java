package jxsource.oauth2.client.jwt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import jxsource.oauth2.jwt.util.ClientJwtUtil;

/*
 * This is REST service to generate JWT token for a client to use when
 * getting access token from Authorization server using client_cridentials of grant type
 */
@SpringBootApplication
public class ClientJwtGenerateUrl {

	public static void main(String... args) {
		SpringApplication.run(ClientJwtGenerateUrl.class, args);
	}

	@Bean
	public ClientJwtUtil createClientJwtUtil() {
		return new ClientJwtUtil();
	}

}
