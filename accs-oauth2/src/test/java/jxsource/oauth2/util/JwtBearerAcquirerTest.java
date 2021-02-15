package jxsource.oauth2.util;

import java.io.IOException;
import java.net.URI;
import java.util.UUID;

import org.springframework.web.util.UriComponentsBuilder;

public class JwtBearerAcquirerTest {

//	String authHost = "https://devws.ba.ssa.gov:447";
	String authHost = "http://localhost:8901";
	String user = "mydomain";
	String scope = "/accs/availability-srvc";
	
	private void test(KeyInfo keyInfo) throws IOException {
		

		URI	tokenEndpointUri = UriComponentsBuilder.fromUriString(authHost+"/oauth/token")
//		URI	tokenEndpointUri = UriComponentsBuilder.fromUriString(authHost+"/oauth/token?client_id="+user+"&grant_type=client_credentials")
				.build().toUri();

		JwtBearerAcquirer jwtBearerAcquirer = JwtBearerAcquirer.builder().setPrivateKeySigner(keyInfo.getPrivateKey())
				.setIssuer(user)
				.setScope(scope)
				.setJwtId(UUID.randomUUID())
				.setTokenEndpoint(tokenEndpointUri)
				.build();
		String accessToken = jwtBearerAcquirer.getJwtBearerToken();

	}
}
