package jxsource.oauth2.resourceServer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;


@SpringBootApplication
@EnableOAuth2Client
//TODO: search for function of annotation below. uncomment makes the service fails
//@EnableGlobalMethodSecurity(securedEnabled=true, jsr250Enabled=true, prePostEnabled=true)
public class ResourceServer {

	public static void main(String[] args) {
//TODO: ? may related to annotation issue
//		System.setProperty("role.update","ROLE_UPDATE");
		SpringApplication.run(ResourceServer.class, args);
	}
}
