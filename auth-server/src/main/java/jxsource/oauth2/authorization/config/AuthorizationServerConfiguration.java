package jxsource.oauth2.authorization.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.Filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import jxsource.oauth2.util.KeyFactory;
import jxsource.oauth2.util.KeyInfo;
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

    public static final String TOKEN_BASED_AUTH_ENTRY_POINT = "/oauth/token";

	@Value("${resource.id:spring-boot-application}")
    private String resourceId;
    
    @Value("${access_token.validity_period:3600}")
    int accessTokenValiditySeconds = 3600;

    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
    	JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
//    	KeyInfo keyInfo = new KeyInfoJKSFactory().create();
    	KeyInfo keyInfo = KeyFactory.createKeyInfo();
 	   converter.setKeyPair(keyInfo.getKeyPair());
		return converter;
    }

    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(accessTokenConverter());
    }

    protected JwtTokenAuthenticationProcessingFilter buildJwtTokenAuthenticationProcessingFilter() throws Exception {
        List<String> pathsToSkip = Arrays.asList("NoSkipPath");
        SkipPathRequestMatcher matcher = new SkipPathRequestMatcher(pathsToSkip, TOKEN_BASED_AUTH_ENTRY_POINT);
        JwtTokenAuthenticationProcessingFilter filter 
            = new JwtTokenAuthenticationProcessingFilter(null , matcher);
        filter.setAuthenticationManager(this.authenticationManager);
        return filter;
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.tokenStore(tokenStore())
            .authenticationManager(this.authenticationManager)
            .accessTokenConverter(accessTokenConverter());
    }
    
    @Override
    public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
    	List<Filter> filters = new ArrayList<Filter>();
    	filters.add(this.buildJwtTokenAuthenticationProcessingFilter());
        oauthServer.tokenEndpointAuthenticationFilters(filters);
        oauthServer.tokenKeyAccess("isAnonymous()");
//        oauthServer
//        	.tokenKeyAccess("isAnonymous() || hasAuthority('ROLE_TRUSTED_CLIENT')")
//            .checkTokenAccess("hasAuthority('ROLE_TRUSTED_CLIENT')");
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
       clients.inMemory()
            .withClient("mydomain")
                .authorities("DISDEV.9ACCS99USER")
                .scopes("scope_string_without_space")
                .resourceIds(resourceId)
                .authorizedGrantTypes("client_credentials")
                .accessTokenValiditySeconds(accessTokenValiditySeconds)
       .and()
       		.withClient("oauth2test")
       			.authorities("DISDEV.9ACCS9ADMIN")
       			.scopes("scope_string_without_space")
       			.resourceIds(resourceId)
                .authorizedGrantTypes("client_credentials")
       			.accessTokenValiditySeconds(accessTokenValiditySeconds);
    }

}
