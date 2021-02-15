package jxsource.oauth2.util;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class HttpServletRequestJsonNodeBuilder {
	private ObjectMapper mapper = new ObjectMapper();
	private HttpServletRequest request;
	private boolean consumContent;
	public HttpServletRequestJsonNodeBuilder setRequest(HttpServletRequest request) {
		this.request = request;
		return this;
	}
	public HttpServletRequestJsonNodeBuilder loadContent() {
		consumContent = true;
		return this;
	}
	public JsonNode build() throws JsonProcessingException, IOException {
		ObjectNode root = mapper.createObjectNode();
		root.put("method", request.getMethod());
		root.put("uri", request.getRequestURI());
		ObjectNode headerNodes = mapper.createObjectNode();
		Enumeration<String> headers = request.getHeaderNames();
		while(headers.hasMoreElements()) {
			String header = headers.nextElement();
			ObjectNode headerNode = mapper.createObjectNode();
			if(header.equals("authorization")) {
				String[] authArray = request.getHeader(header).split(" ");
				if("Bearer".equals(authArray[0])) {
					JsonNode jwt = JwtUtil.convertToJsonNode(authArray[1]);
					ObjectNode bearer = mapper.createObjectNode();
					bearer.set(authArray[0], jwt);
					headerNodes.set(header,bearer);
				} else {
					headerNodes.put(header, request.getHeader(header));					
				}
			} else {
				headerNodes.put(header, request.getHeader(header));					
			}
		}
		root.set("headers", headerNodes);
		if(consumContent) {
			try {
				Reader in = new InputStreamReader(request.getInputStream());
				char[] cbuf = new char[1024*8];
				int i = 0;
				StringBuilder strBuilder = new StringBuilder();
				while((i=in.read(cbuf))!= -1 ) {
					strBuilder.append(cbuf,0,i);
				}
				String content = strBuilder.toString();
				if(headerNodes.get("content-type") != null &&
					headerNodes.get("content-type").asText().equals("application/json")) {
					ObjectMapper om = new ObjectMapper();
					om.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
					om.configure(JsonGenerator.Feature.QUOTE_FIELD_NAMES, false);
					om.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
					JsonNode contentNode = mapper.readTree(content);
					root.set("content", contentNode);
				} else {
					root.put("content",content);
				}
			} catch(Exception e) {
				e.printStackTrace();
				root.put("content","Error when load content: "+e.getClass().getName()+": "+e.getMessage());
			}
		}
		return root;
	}

}
