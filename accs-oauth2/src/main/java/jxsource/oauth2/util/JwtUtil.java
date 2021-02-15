package jxsource.oauth2.util;

import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.Base64Codec;

public class JwtUtil {
	private static SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static String extractJwt(HttpServletRequest req) {
		String auth = req.getHeader("authorization");
		if (auth == null || auth.trim().length() == 0) {
			try {
				char[] data = new char[req.getContentLength()];
				Reader r = req.getReader();
				r.read(data);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new RuntimeException("Invalid authorization field: "
						+ auth);
			}
			return null;
		} else {
			return extractJwtFromBearer(auth);
		}
	}

	public static String extractJwtFromBearer(String auth) {
		int index = auth.indexOf(" ");
		if (index < 1 || !auth.subSequence(0, index).equals("Bearer")) {
			throw new RuntimeException(
					"authorization field does not start with 'Bearer': " + auth);
		}
		return auth.substring(index + 1);
	}

	public static String jwtView(String enCodedJwt) {
		String decode = jwtBase64Decode(enCodedJwt);
		ObjectMapper om = new ObjectMapper();
		try {
			StringTokenizer st = new StringTokenizer(decode, "}");
			String[] parts = new String[st.countTokens()];
			int i = 0;
			while(st.hasMoreTokens()) {
				parts[i++] = st.nextToken()+'}';
			}
			JsonNode root = om.readTree(parts[parts.length-1].substring(1));
			convertTime(root, "exp");
			convertTime(root, "nbf");
			convertTime(root, "iat");
			decode = "";
			for(int k=0; k<parts.length-1; k++) {
				decode += parts[k];
			}
			decode += '.'+root.toString();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return decode;
	}
	
	public static JsonNode getJwtClaims(String enCodedJwt) throws JsonProcessingException, IOException {
		ArrayNode node = (ArrayNode)convertToJsonNode(enCodedJwt);
		return node.get(node.size()-1);
	}
	
	public static JsonNode convertToJsonNode(String enCodedJwt) throws JsonProcessingException, IOException {
		String decode = jwtBase64Decode(enCodedJwt);
		ObjectMapper om = new ObjectMapper();
			StringTokenizer st = new StringTokenizer(decode, "}");
			String[] parts = new String[st.countTokens()];
			int i = 0;
			while(st.hasMoreTokens()) {
				parts[i++] = st.nextToken()+'}';
			}
			JsonNode body = om.readTree(parts[parts.length-1].substring(1));
			convertTime(body, "exp");
			convertTime(body, "nbf");
			convertTime(body, "iat");
			
			ArrayNode root = om.createArrayNode();
			root.add(parts[0]);
			for(i=1; i<parts.length-1; i++) {
				root.add((parts[i].substring(1)));
			}
			root.add(body);

		return root;
	}

	public static String jwtBase64Decode(String enCodedJwt) {
		String[] jwtParts = enCodedJwt.split("\\.");
		String decode = "";
		int count = 0;
		for (String jwtPart : jwtParts) {
			byte[] part = Base64Codec.BASE64URL.decode(jwtPart);
			if(part[0] == '{') {
				decode += new String(part)+'.';
				count++;
			}
		}		
		return decode.substring(0,decode.length()-1);
	}
	
	public static JsonNode convertTime(JsonNode node, String name) {
		JsonNode vNode = ((ObjectNode)node).remove(name);	
		if(vNode != null) {
			// JWT time is in second
			long rawValue = vNode.longValue();
			Date time = new Date(rawValue*1000);
			String strTime = dFormat.format(time);
			((ObjectNode)node).put(name, rawValue);
			((ObjectNode)node).put(name+"-time", strTime);
		}
		return node;
	}

	public static PublicKey getPublicKey(String host) {
		URI verifierKeyEndpoint = UriComponentsBuilder.fromUriString(host)
				.build().toUri();

		PublicKeyVerifier verifier = PublicKeyVerifier.getBuilder()
				.setVerifierKeyEndpoint(verifierKeyEndpoint).build();
		return verifier.getVerifierKey();
	}

	public static String createJwtToken(String issuer, String subject,
			String tokenEndpoint, PrivateKey signerKey) {
		return createJwtToken(
				60, // long authenticationTokenExpirationSeconds
				issuer, subject, tokenEndpoint, UUID.randomUUID().toString(),
				SignatureAlgorithm.RS256, signerKey);
	}

	public static String createJwtToken(
			long authenticationTokenExpirationSeconds, String issuer,
			String subject, String tokenEndpoint, String jwtId,
			SignatureAlgorithm signatureAlgorithm, PrivateKey signerKey) {
		long nowMillis = System.currentTimeMillis();
		Date exp = new Date(nowMillis
				+ (authenticationTokenExpirationSeconds * 1000));
		Date now = new Date(nowMillis);

		@SuppressWarnings("unchecked")
		JwtBuilder jwtBuilder = Jwts.builder().setIssuer(issuer)
				.setSubject(subject).setExpiration(exp).setIssuedAt(now)
				.setNotBefore(now).setAudience(tokenEndpoint.toString())
				.setId(jwtId.toString())
				.setHeaderParams(Jwts.header().setType(Header.JWT_TYPE))
				.signWith(signatureAlgorithm, signerKey);

		String jwtString = jwtBuilder.compact();
		Jwt<?, ?> parsedJwt = Jwts.parser().setSigningKey(signerKey)
				.parse(jwtString);
		String[] jwtParts = jwtString.split("\\.");
		return jwtString;
	}

	public static HttpEntity<?> createClientJwtRequestContent(String scope,
			String jwtString) {
		MultiValueMap<String, String> form = new LinkedMultiValueMap<String, String>();
		form.add("grant_type", "client_credentials");
		form.add("scope", scope);
		form.add("client_assertion_type",
				"urn:ietf:params:oauth:client-assertion-type:jwt-bearer");
		form.add("client_assertion", jwtString);

		HttpEntity<?> entity = null;
		MediaType mediaType = MediaType.APPLICATION_JSON;
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(mediaType);
		LinkedHashMap<String, String> request = new LinkedHashMap<String, String>();
		for (Entry<String, List<String>> formEntry : form.entrySet()) {
			List<String> formEntryValue = formEntry.getValue();
			request.put(
					formEntry.getKey(),
					(formEntryValue != null && !formEntryValue.isEmpty()) ? formEntryValue
							.get(0) : null);
		}
		ObjectMapper mapper = new ObjectMapper();
		try {
			entity = new HttpEntity<String>(mapper.writeValueAsString(request),
					headers);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(
					"Error when creating HttpEntity<?> with headers: "
							+ headers, e);
		}
		return entity;
	}

}
