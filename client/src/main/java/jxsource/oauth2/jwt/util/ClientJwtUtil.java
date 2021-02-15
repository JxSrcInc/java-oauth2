package jxsource.oauth2.jwt.util;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.net.URI;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.UriComponentsBuilder;

import jxsource.oauth2.util.JwtBearerAcquirer;
import jxsource.oauth2.util.KeyFactory;
import jxsource.oauth2.util.KeyInfo;

@Service
public class ClientJwtUtil {
	private static Logger logger = LoggerFactory.getLogger(ClientJwtUtil.class);
	
	// authorization server host - http(s)://domain:port
	@Value("${oauth.client.authHost}")
	private String authHost;
	// resource server host - http(s)://domain:port
	@Value("${oauth.client.resourceHost}")
	private String resourceHost;
	// resource request path
	@Value("${oauth.client.contextPath}")
	private String contextPath;
	// jwt scope
	@Value("${oauth.client.jwt.scope}")
	private String scope;
	// jwt issuer
	@Value("${oauth.client.jwt.issuer}")
	private String issuer;
	@Value("${oauth.web.gateway}")
	private String webGateway;

	public ClientJwtUtil setUser(String user) {
		this.issuer = user;
		return this;
	}
	public ClientJwtUtil setScope(String scope) {
		this.scope = scope;
		return this;
	}
	public JwtBearerAcquirer getJwtBearerAcquirer() {
		return getJwtBearerAcquirer(authHost, issuer, scope);
	}
	public JwtBearerAcquirer getJwtBearerAcquirer(String authHost, String user, String scope) {
		URI tokenEndpointUri = null;
//		if (authHost.equals("http://localhost:8085")) {
			// keyInfo = new KeyInfoJKSImpl().load();
			tokenEndpointUri = UriComponentsBuilder
					.fromUriString(
							authHost + "/oauth/token"
							// Spring requires grant_type request parameter.
							+"?grant_type=client_credentials"
					).build().toUri();
//		} else {
//			// keyInfo = new KeyInfoPEMImpl().load();
//			tokenEndpointUri = UriComponentsBuilder
//					.fromUriString(authHost + "/oauth/token"
//							).build().toUri();
//		}
		return getJwtBearerAcquirer(tokenEndpointUri, user, scope);
	}
	
	public JwtBearerAcquirer getJwtBearerAcquirer(URI tokenEndpointUri, String user, String scope) {
		KeyInfo keyInfo = KeyFactory.createKeyInfo();
		logger.debug("tokenEndpointUri: " + tokenEndpointUri);

		JwtBearerAcquirer jwtBearerAcquirer = JwtBearerAcquirer.builder()
				.setPrivateKeySigner(keyInfo.getPrivateKey()).setIssuer(user)
				.setScope(scope).setJwtId(UUID.randomUUID())
				.setTokenEndpoint(tokenEndpointUri).build();
		return jwtBearerAcquirer;
	}

	public String getAccessToken(JwtBearerAcquirer jwtBearerAcquirer) throws IOException {
		String accessToken = jwtBearerAcquirer.getJwtBearerToken();

//		logger.debug("accessToken: "
//				+ JwtUtil.jwtView(accessToken));
//		Jws<Claims> parsedClaims = Jwts.parser()
//				.setSigningKey(pubKey)
////				.setAllowedClockSkewSeconds(30)
//				.parseClaimsJws(accessToken);
//		Date jwtBearerTokenExpiration = parsedClaims.getBody().getExpiration();
//		logger.debug("jwtBearerTokenExpiration: "+jwtBearerTokenExpiration);
		return accessToken;
		
	}
	public String getResourceHost() {
		return this.resourceHost;
	}
	// return http(s)://domain:port
	public URI getResourceHostUri() {
		return UriComponentsBuilder.fromUriString(resourceHost)
			.build().toUri();
	}
	// return http(s)://domain.port/context-path
	public URI getResourceEndpointUri() {
		return UriComponentsBuilder
			.fromUri(getResourceHostUri()).path(contextPath).build().toUri();
	}
	public URI getWebGatewayUri() {
		return UriComponentsBuilder.fromUriString(webGateway)
			.build().toUri();
	}
}
