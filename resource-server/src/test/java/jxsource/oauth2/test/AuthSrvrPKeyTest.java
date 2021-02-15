package jxsource.oauth2.test;

import java.io.InputStream;
import java.net.URL;
import java.util.Map;

import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import com.fasterxml.jackson.databind.ObjectMapper;

public class AuthSrvrPKeyTest {
	
	public static void main(String...args) {
		String tokenKeyEndpoint = "https://devws.ba.ssa.gov:447/oauth/token_key";
		JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
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
				TokenStore tokenStore = new JwtTokenStore(converter);		
//		        OAuth2Authentication auth = tokenStore.readAuthentication(tokenValue);
//		        Map<String, Object> details = (Map<String, Object>) auth.getDetails();
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				throw new RuntimeException(e);
			}
	  	
		
	}

}
