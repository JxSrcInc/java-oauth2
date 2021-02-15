package jxsource.oauth2.util;

import static org.junit.Assert.*;

import java.security.PublicKey;

import org.junit.Ignore;
import org.junit.Test;

import jxsource.oauth2.util.JwtUtil;

public class PublickeyVerifierTest {

	@Test
	@Ignore
	public void testMDE() {
		assertPublicKey(JwtUtil.getPublicKey("https://devws.ba.ssa.gov:447/oauth/token_key"));
	}
	@Test
	public void testLocal() {
		assertPublicKey(JwtUtil.getPublicKey("http://localhost:8901/oauth/token_key"));
	}
	
	private void assertPublicKey(PublicKey key) {
		System.out.println(key);
		assertNotNull(key);
		
	}
}
