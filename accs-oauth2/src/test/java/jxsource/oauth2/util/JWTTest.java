package jxsource.oauth2.util;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import jxsource.oauth2.util.JwtUtil;

public class JWTTest {
	ObjectMapper om = new ObjectMapper();
	String jwt 	= "eyJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJodHRwczovL2RldndzLmJhLnNzYS5nb3Y6NDQ3L29hdXRoL3Rva2VuIiwic3ViIjoiOUUyNTgzIiwiZXhwIjoxNTEwMTY2OTg4LCJuYmYiOjE1MTAxNjUxODgsImlhdCI6MTUxMDE2NTE4OCwianRpIjoiODQ3ODBmMzYtNjk3Zi00YzUxLWJiYjUtNjcwNWY4NWRhODM3IiwiYXV0aG9yaXRpZXMiOiJESVNERVYuOUFDQ1M5QURNSU4iLCJhdWQiOiJodHRwczovL2FjY3MtYXZhaWxhYmlsaXR5LXNydmMtYWNjcy1kZXYuYWNpcGFhcy1kZXYuc3NhLmdvdiIsInNjb3BlIjoiL2FjY3MvYXZhaWxhYmlsaXR5LXNydmMifQ.WnUFClfbTi_96MzLUp7anILo3ZAIXS0FqEoC9s27xh2r99QFTG72Wum4dBVBfyfssQybMHuWYICPBNfururevNvjG654RUE-y1mAls7Jz0MdYfhsJOGXT3lJm823Q6eppxGB8SnKDBeI2zbxKi9xFxZ-zCqAVFhIL73QXiabpntCUs4J9S6wsiCUPJ83rl_eOLp9FA41HKKSZt8c_i_UvjMPgV86hlJ6AjIv8tE88NnhIV0GYbJM5LJZkiGEgUtss-ciNrjBAIOxAPSCXppRxJi116hGT93k8bwsxyx91TrqQctDtZMEDFzC40XZwyXc5dgLuRJW5mlGzo5F0aHavg";
	String jwt1 = "eyJhbGciOiJSUzI1NiJ9.eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOlsiaHR0cHM6Ly9hY2NzLWF2YWlsYWJpbGl0eS1zcnZjLWFjY3MtZGV2LmFjaXBhYXMtZGV2LnNzYS5nb3YiXSwic2NvcGUiOlsiL2FjY3MvYXZhaWxhYmlsaXR5LXNydmMiXSwiZXhwIjoxNTEwMTcxNjk3LCJhdXRob3JpdGllcyI6WyJESVNERVYuOUFDQ1M5QURNSU4iXSwianRpIjoiNjIwYTVjZTQtZDljNy00NDY0LWJlN2ItOTM1NmM2ZWI3NzlmIiwiY2xpZW50X2lkIjoiOUUyNTgzIn0.a_LZ8gHxGEqbpRuVGnuhyit--8ep0S5zR2W5ejsEJdsE8JoQ9fbnrPyI3w74D-BrPA8UpGEmTc1VS6AGuHBbYV1c9gus5wxyKf1rGHYNjKR79G2N7JjePoDWm1YpEVsmem5s1rTCExhVQPEC6s-RFzB3EPjpXfFKzw9VWGbQTZmu3KwukmRwdUflD4qDBpngbEAp78PXxPPKlWcOOXLNgiWLIeNbtgR3KSizY307HeJ3wy9UjYc8xi7Xh-1-X711TLtB3WoSb0ip6vX0rN_KgCCVgmFbx0n5VyGyZqnTSkdLgCYcLE-O567PFRTdHB4SPa1rZwBFTkpZkM-UYxLjXw.38jauZFIfIh7_-IPHk5Mg5ahJ55uhFAr5ijtroaKmT33UpSHTMt-Z9Y5pm_ap8ix6JW8e1C5HGt3v1myLYGfmX9Zc24E-GvF3iph7iNozE1nl29_7ZuX5UYh5-PIJvU6ZP_-7IMMTKU5JnZ_Z4KE1PIXNX9V3qPQv-s4reoomAxnKGjqri0zsSYsXhQ-5tKc-juvGqH9_pPM7lvIkPU5nPm-C1FQMJl6NHcw3TxVDuMJQTWoVy0RwW9vFccknkcuKG1jyT8rFUQLe3ua3PfWeFBRDkU0AD7UROmBQ0Gy49MIYBmK7lmHcIUDNdwCcGuoxgdvi53L4AS35xEnD1Lfaw";

	@Test
	public void testView() {
		assertNotNull(JwtUtil.jwtView(jwt));
		assertNotNull(JwtUtil.jwtView(jwt1));
	}

	@Test
	public void testConvert() throws JsonProcessingException, IOException {
		verifyJwt(verifyArrayNode(JwtUtil.convertToJsonNode(jwt)));
		verifyJwt(verifyArrayNode(JwtUtil.convertToJsonNode(jwt1)));
	}

	private JsonNode verifyArrayNode(JsonNode node) throws JsonProcessingException {
//		System.out.println(om.writerWithDefaultPrettyPrinter().writeValueAsString(node));
		assertTrue(node instanceof ArrayNode);
		ArrayNode arrayNode = (ArrayNode) node;
		return arrayNode.get(arrayNode.size()-1);
	}
	
	private void verifyJwt(JsonNode jwt) {
		assertNotNull(jwt.get("aud"));
		assertNotNull(jwt.get("authorities"));
		assertNotNull(jwt.get("exp"));
	}
	
	@Test
	public void test() throws JsonProcessingException, IOException {
		JsonNode node = JwtUtil.convertToJsonNode(jwt1);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("Bearer", node);
		System.out.println(om.writerWithDefaultPrettyPrinter().writeValueAsString(map));
	}
}
