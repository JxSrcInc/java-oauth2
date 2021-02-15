package jxsource.oauth2.util;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

import io.jsonwebtoken.impl.Base64Codec;

public class JwtInfo extends HashMap<String,JsonNode>{

	public JwtInfo setJwt(String enCodedJwt) {
		String[] jwtParts = enCodedJwt.split("\\.");
		if(jwtParts.length != 3) {
			throw new RuntimeException("Invalid JWT token: "+enCodedJwt);
		}
		int count = 0;
		for(String jwtPart: jwtParts) {
			count++;
			String decode = new String(Base64Codec.BASE64URL.decode(jwtPart));
			if(count < jwtParts.length) {
				this.putAll(Util.parseJson(decode));
			}
			// exclude signed signature - the part after last dot (.)
			if(count==2) break;
		}
		return this;
	}
	
	@Override
	public String toString() {
		String s = "------ JWT Start ------\n";
		for(Map.Entry<String,JsonNode> entry: this.entrySet()) {
			s += entry.getKey()+" = "+entry.getValue().asText()+"\n";
		}
		s += "------ JWT End ------\n";
		return s;
	}
}
