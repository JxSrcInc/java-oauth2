package jxsource.oauth2.util;

import java.io.IOException;
import java.net.URI;
import java.security.PrivateKey;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

import io.jsonwebtoken.impl.Base64Codec;

/*
 * Get Bearer token from authorization server
 */
public class JwtBearerAcquirer {
	protected final Log logger = LogFactory.getLog(getClass());

	private String issuer;
	private String subject;
	private String scope;
	private URI tokenEndpoint;
	private PrivateKey signerKey;
	private UUID jwtId;
	private String jwtBearerToken;
	private Date jwtBearerTokenExpiration;
	private RestTemplate authenticationRestTemplate;
	private boolean useFormBasedTokenEndpointRequests;
	private boolean includeBearerTokenForAllRequests;
	private long authenticationTokenExpirationSeconds;
	private int allowedClockSkewSeconds = 30;

	private JwtBearerAcquirer(String issuer, String subject, String scope,
			URI tokenEndpoint, PrivateKey privateKeySigner,
			UUID jwtId,
			boolean includeBearerTokenForAllRequests,
			long authenticationTokenExpirationSeconds, int allowedClockSkewSeconds) {
		this.issuer = issuer;
		this.subject = subject;
		this.scope = scope;
		this.tokenEndpoint = tokenEndpoint;
		this.signerKey = privateKeySigner;
		this.jwtId = jwtId;
		this.authenticationRestTemplate = new RestTemplate();
		this.includeBearerTokenForAllRequests = includeBearerTokenForAllRequests;
		this.authenticationTokenExpirationSeconds = authenticationTokenExpirationSeconds;
		this.allowedClockSkewSeconds = allowedClockSkewSeconds;
	}

	public static JwtBearerAcquirerBuilder builder() {
		return new JwtBearerAcquirerBuilder();
	}

	public static class JwtBearerAcquirerBuilder {
		private String issuer;
		private String subject;
		private String scope;
		private URI tokenEndpoint;
		private PrivateKey privateKeySigner;
		private UUID jwtId;
		private boolean includeBearerTokenForAllRequests = false;
		private long authenticationTokenExpirationSeconds = 60;
		private int allowedClockSkewSeconds = 30;

		public JwtBearerAcquirerBuilder setIssuer(String issuer) {
			this.issuer = issuer;
			return this;
		}

		public JwtBearerAcquirerBuilder setSubject(String subject) {
			this.subject = subject;
			return this;
		}

		public JwtBearerAcquirerBuilder setScope(String scope) {
			this.scope = scope;
			return this;
		}

		public JwtBearerAcquirerBuilder setTokenEndpoint(URI tokenEndpoint) {
			this.tokenEndpoint = tokenEndpoint;
			return this;
		}

		public JwtBearerAcquirerBuilder setPrivateKeySigner(
				PrivateKey privateKeySigner) {
			this.privateKeySigner = privateKeySigner;
			return this;
		}

		public JwtBearerAcquirerBuilder setJwtId(UUID jwtId) {
			this.jwtId = jwtId;
			return this;
		}

		@Deprecated
		public JwtBearerAcquirerBuilder setUseFormBasedTokenEndpointRequests(
				boolean useFormBasedTokenEndpointRequests) {
			return this;
		}

		public JwtBearerAcquirerBuilder setIncludeBearerTokenForAllRequests(
				boolean includeBearerTokenForAllRequests) {
			this.includeBearerTokenForAllRequests = includeBearerTokenForAllRequests;
			return this;
		}

		public JwtBearerAcquirerBuilder setAuthenticationTokenExpirationSeconds(
				long authenticationTokenExpirationSeconds) {
			this.authenticationTokenExpirationSeconds = authenticationTokenExpirationSeconds;
			return this;
		}

		public JwtBearerAcquirerBuilder setAllowedClockSkewSeconds(
				int allowedClockSkewSeconds) {
			this.allowedClockSkewSeconds = allowedClockSkewSeconds;
			return this;
		}

		public JwtBearerAcquirer build() {
			Assert.hasText(issuer, "Issuer cannot be null or empty");
			subject = (StringUtils.hasText(subject)) ? subject : issuer;
			Assert.hasText(scope, "Scope cannot be null or empty");
			Assert.notNull(tokenEndpoint, "Token endpoint cannot be null");
			Assert.notNull(privateKeySigner, "Private key signer cannot be null");
			Assert.notNull(jwtId, "JWT id cannot be null");
			Assert.isTrue(authenticationTokenExpirationSeconds > 0,
					"Authentication token expiration seconds must be greater than 0");
			Assert.isTrue(allowedClockSkewSeconds >= 0,
					"Allowed clock skew seconds must be greater than or equal to 0");
			Assert.isTrue(allowedClockSkewSeconds <= 60 * 5,
					"Allowed clock skew seconds must be less than or equal to 300 seconds (5 minutes)");

			return new JwtBearerAcquirer(issuer, subject, scope, tokenEndpoint,
					privateKeySigner, 
					UUID.randomUUID(),
					includeBearerTokenForAllRequests,
					authenticationTokenExpirationSeconds, allowedClockSkewSeconds);
		}
	}

	/**
	 * @return jwtBearerToken that can be used as part of a
	 * <code>Authorization: Bearer jwtBearerToken</code> header
	 * @throws IOException 
	 */
	public String getJwtBearerToken() throws IOException {
		synchronized (subject) {
			if (jwtBearerToken != null && jwtBearerTokenExpiration != null) {
				Date oneMinuteFromNow = new Date(System.currentTimeMillis() + 60 * 1000);
				if (oneMinuteFromNow.after(jwtBearerTokenExpiration)) {
					logger.debug("JWT Bearer token was either expired or expiring soon for subject: "
							+ subject);
					// Null it out so a new one is requested
					jwtBearerToken = null;
				}
			}
			if (jwtBearerToken == null) {
				logger.debug("Requesting new JWT Bearer token for subject: " + subject);
				jwtBearerToken = acquireJwtBearerToken();
				JsonNode jwtNode = JwtUtil.getJwtClaims(jwtBearerToken);
				Long expirationDate = jwtNode.get("exp").asLong();
				jwtBearerTokenExpiration = new Date(expirationDate*1000);
			}
		}
		return jwtBearerToken;
	}
	public  String jwtBase64Decode(String jwt) {
		String[] jwtParts = jwt.split("\\.");
		String decode = "";
		int count = 0;
		for(String jwtPart: jwtParts) {
			count++;
			decode = new String(Base64Codec.BASE64URL.decode(jwtPart));
			if(count < jwtParts.length) {
				decode += '.';
			}
		}
		return decode;
	}

	public URI getTokenEndpoint() {
		return tokenEndpoint;
	}

	public RestTemplate getAuthenticationRestTemplate() {
		return authenticationRestTemplate;
	}

	public boolean isIncludeBearerTokenForAllRequests() {
		return includeBearerTokenForAllRequests;
	}

	private String acquireJwtBearerToken() throws JsonProcessingException {
		
		String jwtString = JwtUtil.createJwtToken(issuer, subject, tokenEndpoint.toString(), signerKey);
		logger.debug("Request Jwt: "+JwtUtil.jwtView(jwtString));
		HttpEntity<?> entity = JwtUtil.createClientJwtRequestContent(scope, jwtString);
		Map<?, ?> body = null;
		try{
		ResponseEntity<Map> response = authenticationRestTemplate.exchange(tokenEndpoint,
				HttpMethod.POST, entity, Map.class);
		logger.debug("Authorization server "+Util.getResponse(response));
		 body = response.getBody();
		} catch(Exception e) {
			e.printStackTrace();
		}
		String jwtBearerToken = (String) body.get("access_token");
		return jwtBearerToken;
	}
}
