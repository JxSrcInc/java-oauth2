package jxsource.oauth2.authorization;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@ImportResource("classpath:app-config.xml") 
public class AuthorizationServer {

	public static void main(String[] args) {
		SpringApplication.run(AuthorizationServer.class, args);
	}
}
