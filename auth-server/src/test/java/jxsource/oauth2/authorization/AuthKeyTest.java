package jxsource.oauth2.authorization;

import java.net.MalformedURLException;
import java.net.URL;

public class AuthKeyTest {

	public static void main(String...args) {
		try {
			URL url = new URL("http://localhost:8085/oauth/tokey_key");
			System.out.println(url.getContent());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
