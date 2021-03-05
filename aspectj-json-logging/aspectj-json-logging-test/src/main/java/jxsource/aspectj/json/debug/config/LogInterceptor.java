package jxsource.aspectj.json.debug.config;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.StreamUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jxsource.aspectj.json.debug.AfterCall;
import jxsource.aspectj.json.debug.BeforeCall;
import jxsource.aspectj.json.debug.CallLog;
import jxsource.aspectj.json.debug.JsonFactory;
import jxsource.aspectj.json.debug.JsonNodeFactoryImpl;
import jxsource.aspectj.json.debug.RetToJson;
import jxsource.aspectj.json.debug.ThreadLog;
import jxsource.aspectj.json.debug.ThreadLogLocal;


public class LogInterceptor extends HandlerInterceptorAdapter {
	private static final Logger logger = LogManager.getLogger(LogInterceptor.class);
	private ObjectMapper om = new ObjectMapper();
	private String appName;
	JsonNodeFactoryImpl jsonFactory;

	public LogInterceptor(String appName) {
		super();
//		om.configure(JsonGenerator.Feature.QUOTE_FIELD_NAMES, false);
		om.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		this.appName = appName;
		jsonFactory = new JsonNodeFactoryImpl();
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		String id = UUID.randomUUID().toString();
		// init log
		ThreadLog threadLog = ThreadLogLocal.get().init(this.appName+'('+id+')');
		threadLog.setHeader("method", request.getMethod())
			.setHeader("url", request.getRequestURL().toString())
			.setHeader("queryString", request.getQueryString())
			.setHeader("RequestId", request.getHeader("RequestId"))
			.setHeader("Bearer", request.getHeader("Authorization"));
	        InputStream requestInputStream = request.getInputStream();
	        byte[] cachedBody = StreamUtils.copyToByteArray(requestInputStream);
	        try {
	        	threadLog.setRequestBody(new String(cachedBody));
	        } catch(Exception e) {
	        	String err = "Fail to get request body: "+e.getClass().getName()+", "+e.getMessage();
	        	threadLog.setRequestBody(err);
	        	
	        }
			return super.preHandle(request, response, handler);
	}
	
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
			Exception ex) {
		ThreadLog threadLog = ThreadLogLocal.get();
			ObjectNode root = (ObjectNode)threadLog.getJsonNode(jsonFactory);
//			List<CallLog> stack = threadLog.getStack();
//			CallLog last = stack.get(stack.size()-1);
//			if(!(last instanceof BeforeCall)) {
//				JsonNode retNode = RetToJson.build().convert(last);
//				root.set("response-json", retNode);
//			}
//			
			String json = "";
			try {
				json = om.writerWithDefaultPrettyPrinter().writeValueAsString(root);
			} catch (JsonProcessingException e) {
				json = threadLog.getJsonString();
			}
			logger.info(json);
		
	}



}
