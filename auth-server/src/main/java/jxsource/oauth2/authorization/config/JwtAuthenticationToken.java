package jxsource.oauth2.authorization.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

//import com.svlada.security.model.UserContext;
//import com.svlada.security.model.token.RawAccessJwtToken;

/**
 * An {@link org.springframework.security.core.Authentication} implementation
 * that is designed for simple presentation of JwtToken.
 * 
 * @author vladimir.stankovic
 *
 *         May 23, 2016
 */
public class JwtAuthenticationToken extends AbstractAuthenticationToken {
    private static final long serialVersionUID = 2877954820905567501L;

    private Jws<Claims> rawAccessToken;
    private UserContext userContext;

    public JwtAuthenticationToken(Jws<Claims> unsafeToken) {
        super(null);
        this.rawAccessToken = unsafeToken;
        this.setAuthenticated(false);
    }

    public JwtAuthenticationToken(UserContext userContext, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.eraseCredentials();
        this.userContext = userContext;
        super.setAuthenticated(true);
    }

    @Override
    public void setAuthenticated(boolean authenticated) {
        if (authenticated) {
            throw new IllegalArgumentException(
                    "Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
        }
        super.setAuthenticated(false);
    }

    @Override
    public Object getCredentials() {
        return rawAccessToken;
    }

    @Override
    public Object getPrincipal() {
        return (this.userContext==null?"":this.userContext.getUsername());
    }

    @Override
    public void eraseCredentials() {        
        super.eraseCredentials();
        this.rawAccessToken = null;
    }

	@Override
	public String toString() {
		return "JwtAuthenticationToken [rawAccessToken=" + rawAccessToken
				+ ", userContext=" + userContext + "]";
	}
    
}
