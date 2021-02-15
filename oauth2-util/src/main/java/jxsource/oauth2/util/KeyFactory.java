package jxsource.oauth2.util;

import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.cert.Certificate;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
/*
 * Only support jks key store.
 * need add more load methods for other key store, like OpenSSL.
 */
@Service
public class KeyFactory {

	private static KeyFactory me;
	private KeyInfo keyInfo;
	private Certificate certificate;

	private KeyFactory() {
		// keystore used by authorization server to 
		// jwt for access token
		String keystorePath = System.getProperty("oauth2.keystorePath");
		String keystoreAlias = System.getProperty("oauth2.keystoreAlias");
		String keystorePassword = System.getProperty("oauth2.keystorePassword");
		// keystore client used to create jwt request
		// authorization server only needs certificate
		// But this server loads keystore and then extract certificate from the store
		// because client and auth server on the same machine 
		// -- avoid to maintain a separate certificate.
		String certificatePath = System.getProperty("oauth2.certificatePath");
		String certificateAlias = System.getProperty("oauth2.certificateAlias");
		String certificatePassword = System.getProperty("oauth2.certificatePassword");
		keyInfo = load(keystoreAlias, keystorePath, keystorePassword);
		if(certificatePath != null && certificateAlias != null && certificatePassword != null) {
			certificate = load(certificateAlias, certificatePath, certificatePassword).getCert();
		}
	}

	public static KeyInfo createKeyInfo() {
		if (me == null) {
			me = new KeyFactory();
		}
		return me.keyInfo;
	}

	public static Certificate createCertificate() {
		if (me == null) {
			me = new KeyFactory();
		}
		return me.certificate;
	}

	/*
	 * Load KeyInfo from a jks file in classpath with alias, keystorepath and a
	 * single password for both alias and keystored
	 */
	public KeyInfo load(String alias, String keystorepath, String password) {
		KeyInfo info = new KeyInfo();
		try {
			KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
			keystore.load(new ClassPathResource(keystorepath).getInputStream(), password.toCharArray());

			Key key = keystore.getKey(alias, password.toCharArray());
			if (key instanceof PrivateKey) {
				info.setPrivateKey((PrivateKey) key);
				// Get certificate of public key
				info.setCert(keystore.getCertificate(alias));

				// Get public key
				info.setPublicKey(info.getCert().getPublicKey());

				return info;
			} else {
				throw new KeyStoreException(alias + " in " + keystorepath + " is not PrivateKey.");
			}
		} catch (Exception e) {
			throw new RuntimeException("Error when loading JKS file " + keystorepath, e);
		}
	}

}
