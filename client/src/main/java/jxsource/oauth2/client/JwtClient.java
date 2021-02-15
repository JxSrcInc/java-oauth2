package jxsource.oauth2.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jxsource.oauth2.jwt.util.ClientJwtUtil;
import jxsource.oauth2.util.JwtBearerAcquirer;


//@SpringBootApplication(exclude = {
//		EmbeddedServletContainerAutoConfiguration.class,
//		WebMvcAutoConfiguration.class })
@SpringBootApplication
public class JwtClient implements CommandLineRunner, Runnable  {
	private static Logger logger = LoggerFactory.getLogger(JwtClient.class);
	ObjectMapper mapper = new ObjectMapper();
	private String ssaee_pin = "123456";
	JwtBearerAcquirer jwtBearerAcquirer;
//	URI resourceEndpointUri; 
//	PublicKey pubKey;
	private int repeate = 1;
	
	@Autowired
	ClientJwtUtil jwtUtil;
	
	@Bean
	public ClientJwtUtil createClientJwtUtil() {
		return new ClientJwtUtil();
	}
	public static void main(String... args) {
		//SpringApplication.run(JwtClient.class, args);
		new SpringApplicationBuilder(JwtClient.class)
		  .web(WebApplicationType.NONE)
		  .run(args);
	}
	
	public void run(String... args) throws Exception {
		jwtBearerAcquirer =jwtUtil.getJwtBearerAcquirer();
		new Thread(this).start();
	}

	public void run() {	
		for (int i = 0; i < repeate; i++) {

		try {
//		String accessToken = jwtUtil.getAccessToken(jwtBearerAcquirer);
		String accessToken = getAccessToken();
		int sleepTime = 1000*10;//random.nextInt(2000);
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + accessToken);
		headers.add("Content-Type", "application/json");
		headers.add("SSAEE_PIN", ssaee_pin);

		RestTemplate resourceTemplate = new RestTemplate();
		resourceTemplate.setErrorHandler(new ClientResponseErrorHandler());
		HttpEntity<String> entity = new HttpEntity<String>(
				"" /* empty content */, headers);
			ResponseEntity<String> response = resourceTemplate
					.exchange(jwtUtil.getResourceEndpointUri(), HttpMethod.GET, entity,
							String.class);
			JsonNode requestInfo = mapper.readTree(response.getBody());
//			JsonNode requestJwt = ((ArrayNode)((ArrayNode)requestInfo.get("headers")).findValue("authorization").get("Bearer")).get(1);
			logger.debug(Thread.currentThread().getName()+"("+i+",sleep="+sleepTime+"): Result - status (" + response.getStatusCode()
					+ ") has body: " + requestInfo.toString());
			if(i < repeate-1 && sleepTime > 0)
				Thread.sleep(sleepTime);
		} catch(Exception e) {
			e.printStackTrace();
//			throw new RuntimeException(e);
		}
		}
	}

	private String getAccessToken() {
		RestTemplate resourceTemplate = new RestTemplate();
		resourceTemplate.setErrorHandler(new ClientResponseErrorHandler());
		HttpEntity<String> entity = new HttpEntity<String>("" /* empty content */);
			ResponseEntity<String> response = resourceTemplate
					.exchange(jwtUtil.getWebGatewayUri(), HttpMethod.GET, entity,
							String.class);
		String token = response.getBody().substring("access_token=".length());

		return token;
	}
}
