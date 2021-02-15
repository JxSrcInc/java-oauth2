package jxsource.oauth2.authorization.config;

import java.io.IOException;
import java.io.InputStream;
import java.security.PublicKey;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.util.matcher.RequestMatcher;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import jxsource.oauth2.util.JwtUtil;
import jxsource.oauth2.util.KeyFactory;

/**
 * Performs validation of provided JWT Token.
 * 
 * @author vladimir.stankovic
 *
 * Aug 5, 2016
 */
public class JwtTokenAuthenticationProcessingFilter extends AbstractAuthenticationProcessingFilter {
    private final AuthenticationFailureHandler failureHandler;

    @Autowired
    KeyFactory keyInfoJKSFactory;

    @Autowired
    public JwtTokenAuthenticationProcessingFilter(AuthenticationFailureHandler failureHandler, 
            RequestMatcher matcher) {
        super(matcher);
        this.failureHandler = failureHandler;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {
    	ObjectMapper mapper = new ObjectMapper();
    	// client JWT is passed in request body
    	InputStream in = request.getInputStream();
    	JsonNode root = mapper.readTree(in);
    	String clientJwt = root.get("client_assertion").asText();
		System.err.println("Decoded JWT: "+JwtUtil.jwtBase64Decode(clientJwt));	
//			KeyPair keyPair = KeyFactory.createKeyInfo().getKeyPair();
//			KeyPair keyPair = new KeyInfoJKSFactory().create().getKeyPair();
//			KeyPair keyPair = new KeyInfoPEMImpl().load().getKeyPair();
			PublicKey pubKey = KeyFactory.createCertificate().getPublicKey();
			Jws<Claims> parsedClaims = Jwts.parser()
					.setSigningKey(pubKey)
		// TODO: what is this?
		//			.setAllowedClockSkewSeconds(allowedClockSkewSeconds)
					.parseClaimsJws(clientJwt);
        Authentication a = getAuthenticationManager().authenticate(new JwtAuthenticationToken(parsedClaims));
        return a;
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
            Authentication authResult) throws IOException, ServletException {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authResult);
        SecurityContextHolder.setContext(context);
        chain.doFilter(request, response);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException failed) throws IOException, ServletException {
    	failed.printStackTrace();
        SecurityContextHolder.clearContext();
        failureHandler.onAuthenticationFailure(request, response, failed);
    }

}
