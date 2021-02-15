package jxsource.oauth2.util;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;

public class KeyInfo {

	protected PublicKey publicKey;
	protected PrivateKey privateKey;
	protected KeyPair keyPair;
	// Certificate associated with this key defined in key store.
	protected Certificate cert;
	
//	public abstract KeyInfo load(String alias, String keystorepath, String password);

	public PublicKey getPublicKey() {
		return publicKey;
	}

	public Certificate getCert() {
		return cert;
	}
	public PrivateKey getPrivateKey() {
		return privateKey;
	}

	public KeyPair getKeyPair() {
		if (keyPair == null) {
			keyPair = new KeyPair(publicKey, privateKey);
		}
		return keyPair;
	}

	public void setPublicKey(PublicKey publicKey) {
		this.publicKey = publicKey;
	}

	public void setPrivateKey(PrivateKey privateKey) {
		this.privateKey = privateKey;
	}

	public void setCert(Certificate cert) {
		this.cert = cert;
	}

}
