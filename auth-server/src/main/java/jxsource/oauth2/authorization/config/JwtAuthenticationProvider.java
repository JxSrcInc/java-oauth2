package jxsource.oauth2.authorization.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

/**
 * An {@link AuthenticationProvider} implementation that will use provided
 * instance of {@link JwtToken} to perform authentication.
 * 
 * @author vladimir.stankovic
 *
 * Aug 5, 2016
 */
@Component
@SuppressWarnings("unchecked")
public class JwtAuthenticationProvider implements AuthenticationProvider {

	static Logger logger = LoggerFactory.getLogger(JwtAuthenticationProvider.class);

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    	logger.debug("Request Authentication: "+authentication.getCredentials());

        Jws<Claims> jwsClaims = (Jws<Claims>)authentication.getCredentials();
        String subject = jwsClaims.getBody().getSubject();
        // The scopes are assigned to UserContext. 
        // But they are not used any where in process.
        List<String> scopes = new ArrayList<String>(Arrays.asList("read, write".split(",")));
        List<GrantedAuthority> authorities = scopes.stream()
                .map(authority -> new SimpleGrantedAuthority(authority))
                .collect(Collectors.toList());
        
        UserContext context = UserContext.create(subject, authorities);
        
        Authentication jwtAuthentication = new JwtAuthenticationToken(context, context.getAuthorities());
    	logger.debug("Authenticated: "+jwtAuthentication);
        return jwtAuthentication;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (JwtAuthenticationToken.class.isAssignableFrom(authentication));
    }

}
