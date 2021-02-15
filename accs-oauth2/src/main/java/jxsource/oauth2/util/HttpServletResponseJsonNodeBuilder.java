package jxsource.oauth2.util;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collection;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class HttpServletResponseJsonNodeBuilder {
	private ObjectMapper mapper = new ObjectMapper();
	private HttpServletResponse response;
	private boolean consumContent;
	public HttpServletResponseJsonNodeBuilder setResponse(HttpServletResponse request) {
		this.response = request;
		return this;
	}
	public JsonNode build() throws JsonProcessingException, IOException {
		ObjectNode root = mapper.createObjectNode();
		ArrayNode headerNodes = mapper.createArrayNode();
		Collection<String> headers = response.getHeaderNames();
		for(String header: headers) {
			ObjectNode headerNode = mapper.createObjectNode();
			if(header.equals("authorization")) {
				String[] authArray = response.getHeader(header).split(" ");
				if("Bearer".equals(authArray[0])) {
					JsonNode jwt = JwtUtil.convertToJsonNode(authArray[1]);
					ObjectNode bearer = mapper.createObjectNode();
					bearer.set(authArray[0], jwt);
					headerNode.set(header,bearer);
				} else {
					headerNode.put(header, response.getHeader(header));					
				}
			} else {
				headerNode.put(header, response.getHeader(header));					
			}
			headerNodes.add(headerNode);
		}
		root.put("headers", headerNodes);
		return root;
	}

}
