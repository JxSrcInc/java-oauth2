package jxsource.oauth2.echoserver.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jxsource.oauth2.util.HttpServletRequestJsonNodeBuilder;
import jxsource.oauth2.util.Util;

@Controller
public class EchoController {
	private static Logger logger = LoggerFactory.getLogger(EchoController.class);
	ObjectMapper mapper = new ObjectMapper();
	@CrossOrigin(origins = "*")
	@RequestMapping("/**")
	@ResponseBody
	public ResponseEntity<String> echoRequest(  
            HttpServletRequest request) throws JsonProcessingException, IOException {
    	JsonNode info = new HttpServletRequestJsonNodeBuilder()
    			.setRequest(request).loadContent().build();
    	String msg = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(info);
		logger.debug("\n"+msg);		
    	HttpHeaders respHeaders = new HttpHeaders();
    	respHeaders.setContentType(MediaType.TEXT_PLAIN);
    	// TODO: remove 
//    	respHeaders.setAccessControlAllowOrigin("*");
		ResponseEntity<String> response = new ResponseEntity<String>(msg, respHeaders, HttpStatus.OK);
		logger.debug("\n"+Util.getResponse(response));		
		return response;
	}

	@CrossOrigin(origins = "*")
	@RequestMapping("/chunk")
	@ResponseBody
	public ResponseEntity<String> testChunk(  
            HttpServletRequest request) throws JsonProcessingException, IOException {
		StringBuilder builder = new StringBuilder();
		for(int i=0; i<100; i++) {
			builder.append("1234567890");
		}
    	String msg = builder.toString();	
    	HttpHeaders respHeaders = new HttpHeaders();
    	respHeaders.setContentType(MediaType.TEXT_PLAIN);
    	respHeaders.add("Transfer-Encoding","chunked");
   	// TODO: remove 
//    	respHeaders.setAccessControlAllowOrigin("*");
		ResponseEntity<String> response = new ResponseEntity<String>(msg, respHeaders, HttpStatus.OK);
		System.out.println("message length: "+msg.length());		
		return response;
	}

	@CrossOrigin(origins = "*")
	@RequestMapping("/html")
	@ResponseBody
	public ResponseEntity<String> html(  
            HttpServletRequest request) throws JsonProcessingException, IOException {
    	String msg = new String(Files.readAllBytes(Paths.get("data\\test.html"))); 	
    	HttpHeaders respHeaders = new HttpHeaders();
    	respHeaders.setContentType(MediaType.TEXT_HTML);
    	// TODO: remove 
//    	respHeaders.setAccessControlAllowOrigin("*");
		ResponseEntity<String> response = new ResponseEntity<String>(msg, respHeaders, HttpStatus.OK);
		System.out.println(msg);		
		return response;
	}

	@ExceptionHandler(Exception.class) 
	   public ResponseEntity<String> exceptionHandler(Exception ex) { 
			logger.error("Controller Error", ex);
			ResponseEntity<String> responseEntity = new ResponseEntity<String>("Controller Error", HttpStatus.INTERNAL_SERVER_ERROR);
			return responseEntity;
		}

}
