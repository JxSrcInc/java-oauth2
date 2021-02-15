package jxsource.oauth2.util;


import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
public class Util {
	public static String getResponse(ResponseEntity<?> response) {
		String msg = "response: -----";
		for(Entry<String, List<String>> e: response.getHeaders().entrySet()) {
			msg += "\n\t"+e.getKey()+" = "+e.getValue();
		}
		msg += "\n\tContent: "+response.getBody();
		return msg;
	}
	public static JsonNode getResponseJson(ResponseEntity<?> response) {
		ObjectMapper om = new ObjectMapper();
		ObjectNode root = om.createObjectNode();
		ObjectNode headers = om.createObjectNode();
		for(Entry<String, List<String>> e: response.getHeaders().entrySet()) {
			ArrayNode values = om.createArrayNode();
			for(String value: e.getValue()) {
				values.add(value);
			}
			headers.set(e.getKey(),values);
		}
		root.set("headers", headers);
		try {
			JsonNode content = om.readTree(response.getBody().toString());
			root.set("content", content);
		} catch (IOException e1) {
			root.put("content", response.getBody().toString());
		}
		return root;
	}
	public static String getHttpRequest(HttpRequest req) {
		String msg = "req: "+req.getURI();
		for(Entry<String, List<String>> e: req.getHeaders().entrySet()) {
			msg += "\n\t"+e.getKey()+" = "+e.getValue();
		}
		return msg;
	}

	public static String toString(InputStream in) throws IOException {
		StringBuilder sb = new StringBuilder();
		int i = 0;
		byte[] buf = new byte[1024*4];
		while((i=in.read(buf))!= -1) {
			sb.append(new String(buf,0,i));
		}
		in.close();
		return sb.toString();
	}
	public static Map<String,JsonNode> parseJson(String json) {
    	ObjectMapper mapper = new ObjectMapper();
    	// client JWT is passed in request body
		try {
			JsonNode root = mapper.readTree(json);
			return parseJson(root);
		} catch (Exception e) {
			throw new RuntimeException("Error when parsing JSON: "+json,e);
		}
	}	
	public static Map<String,JsonNode>  parseJson(InputStream in) {
    	ObjectMapper mapper = new ObjectMapper();
    	// client JWT is passed in request body
		try {
			JsonNode root = mapper.readTree(in);
			return parseJson(root);
		} catch (Exception e) {
			throw new RuntimeException("Error when parsing JSON.",e);
		}
	}	
	
	public static Map<String,JsonNode> parseJson(JsonNode root) {
		Map<String,JsonNode> map = new HashMap<String,JsonNode>();
    	Iterator<String> i = root.fieldNames();
    	while(i.hasNext()){
    		String name = i.next();
    		map.put(name,root.get(name));	
    	}
    	return map;
	}
	
	public static String jsonPrettyPrint(String json) throws JsonProcessingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(mapper.readTree(json));
	}
}
