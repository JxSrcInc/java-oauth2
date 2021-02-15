package jxsource.oauth2.util;

import java.io.StringReader;
import java.net.URI;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Map;

import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.openssl.PEMParser;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

public class PublicKeyVerifier {
	private PublicKey verifierKey;

	private PublicKeyVerifier(PublicKey verifierKey) {
		this.verifierKey = verifierKey;
	}

	public PublicKey getVerifierKey() {
		return verifierKey;
	}

	public static PublicKeySignerBuilder getBuilder() {
		return new PublicKeySignerBuilder();
	}

	public static class PublicKeySignerBuilder {
		private RestTemplate restTemplate = new RestTemplate();
		private URI verifierKeyEndpoint;

//		public PublicKeySignerBuilder setRestTemplate(RestTemplate restTemplate) {
//			this.restTemplate = restTemplate;
//			return this;
//		}
//
		public PublicKeySignerBuilder setVerifierKeyEndpoint(URI verifierKeyEndpoint) {
			this.verifierKeyEndpoint = verifierKeyEndpoint;
			return this;
		}

		public PublicKeyVerifier build() {
			try {
			Assert.notNull(verifierKeyEndpoint, "Verifier key endpoint cannot be null");
			PublicKey verifierKey = null;
				restTemplate = new RestTemplate();

				@SuppressWarnings("unchecked")
				Map<String, Object> verifierResponse = restTemplate.getForObject(
						verifierKeyEndpoint, Map.class);
				String verifierKeyString = (String) verifierResponse.get("value");

				KeyFactory fact = KeyFactory.getInstance("RSA");
				PEMParser pemParser = new PEMParser(new StringReader(verifierKeyString));
				Object object = pemParser.readObject();
				pemParser.close();

				if (object instanceof SubjectPublicKeyInfo) {
					ASN1Primitive parsedPublicKey = ((SubjectPublicKeyInfo) object)
							.parsePublicKey();
					ASN1Sequence seq = ASN1Sequence.getInstance(parsedPublicKey);
					org.bouncycastle.asn1.pkcs.RSAPublicKey key = org.bouncycastle.asn1.pkcs.RSAPublicKey
							.getInstance(seq);
					RSAPublicKeySpec pubSpec = new RSAPublicKeySpec(key.getModulus(),
							key.getPublicExponent());
					verifierKey = fact.generatePublic(pubSpec);
				}

			Assert.notNull(verifierKey, "Verifier key cannot be null");
			return new PublicKeyVerifier(verifierKey);
			} catch(Exception e) {
				throw new RuntimeException("Error when building PublicKeyVerifier.", e);
			}
		}
	}
}
