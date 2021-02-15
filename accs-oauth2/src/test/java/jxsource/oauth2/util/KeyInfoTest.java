package jxsource.oauth2.util;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

import jxsource.oauth2.util.KeyInfo;

public abstract class KeyInfoTest {
	
	static KeyInfo keyInfo;
	
	@Test
	public void testPrivateKey() {
		assertNotNull(keyInfo.getPrivateKey());
	}
	@Test
	public void testPublicKey() {
		assertNotNull(keyInfo.getPublicKey());
	}

}
