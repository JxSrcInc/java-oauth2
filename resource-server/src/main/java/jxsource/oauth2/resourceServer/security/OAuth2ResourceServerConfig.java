package jxsource.oauth2.resourceServer.security;

import java.io.InputStream;
import java.net.URL;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@EnableResourceServer
public class OAuth2ResourceServerConfig extends ResourceServerConfigurerAdapter {
	@Value("${security.oauth2.resource.id}")
	private String resourceId;

	@Value("${security.oauth2.resource.jwt.key.uri}")
	private String tokenKeyEndpoint;
	@Override 
	public void configure(HttpSecurity http) throws Exception { 
 
		http		  
		  	.authorizeRequests()
		  	.anyRequest().authenticated();
	} 
	
	@Override
	public void configure(ResourceServerSecurityConfigurer config) {
		config.tokenServices(tokenServices());
		System.err.println(resourceId);
		config.resourceId(resourceId);
	}

//	@Bean
	public TokenStore tokenStore() {
		// TODO: replace DefaultAccessTokenConverter with CustomJwtAccessTokenConverter
//		jwtAccessTokenConverter.setAccessTokenConverter(new CustomJwtAccessTokenConverter());
//		TokenStore tokenStore = new JwtTokenStore(jwtAccessTokenConverter);		
		TokenStore tokenStore = new JwtTokenStore(accessTokenConverter());		
		return tokenStore;
	}

//	@Autowired
//	JwtAccessTokenConverter jwtAccessTokenConverter;


	@Bean
	@Primary
	public DefaultTokenServices tokenServices() {
		DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
		defaultTokenServices.setTokenStore(tokenStore());
		return defaultTokenServices;
	}
//	@Bean
//	  public JwtAccessTokenConverter accessTokenConverter() {
//		JwtAccessTokenConverter converter = new JxsrcJwtAccessTokenConverter();//new CustomJwtAccessTokenConverter();
//        final Resource resource = new ClassPathResource("public.txt");
//        String publicKey = null;
//        try {
//        publicKey = IOUtils.toString(resource.getInputStream());
//        } catch (final IOException e) {
//        throw new RuntimeException(e);
//        }
//        converter.setVerifierKey(publicKey);
//		return converter;
//	}
/*
 * To use custom JwtAccessTokenConverter	
 * code must call converter.setVerifierKey(pKey) to set authorization server's public key.
 */

	@Bean
	  public JwtAccessTokenConverter accessTokenConverter() {
		JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
	 // code to manually load public key from authorization server
	   	try {
	 System.err.println(tokenKeyEndpoint);
				URL url = new URL(tokenKeyEndpoint);//"http://localhost:8085/oauth/token_key");
				InputStream in = url.openStream();
				byte[] buf = new byte[1024*4];
				String key = new String();
				int i = 0;
				while((i=in.read(buf)) != -1) {
					key += new String(buf, 0, i);
				}
				System.out.println(key);
				ObjectMapper mapper = new ObjectMapper();
				Map<String,String> map = mapper.readValue(key, Map.class);
				String pKey = map.get("value"); 
				System.out.println(pKey);
		        converter.setVerifierKey(pKey);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				throw new RuntimeException(e);
			}
	  	

	  	return converter;
	  }

		
}