package jxsource.oauth2.client.jwt.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jxsource.oauth2.jwt.util.ClientJwtUtil;
import jxsource.oauth2.util.JwtBearerAcquirer;

@Controller
public class ClientJwtGenerateUrlControll {

	@Autowired
	ClientJwtUtil jwtUtil;

	@RequestMapping("/auth")
	@ResponseBody
	public ResponseEntity<String> testJwt() throws IOException {
    	HttpHeaders respHeaders = new HttpHeaders();
    	respHeaders.setContentType(MediaType.TEXT_PLAIN);
		String msg = "access_token="+getToken();
		ResponseEntity<String> response = new ResponseEntity<String>(msg, respHeaders, HttpStatus.OK);
		return response;
	}
	
	private String getToken() throws IOException {
		JwtBearerAcquirer jwtBearerAcquirer =jwtUtil.getJwtBearerAcquirer();
		String accessToken = jwtUtil.getAccessToken(jwtBearerAcquirer);
		return accessToken;
	}
	
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> exceptionHandler(Exception ex)
    {
    	HttpHeaders respHeaders = new HttpHeaders();
    	respHeaders.setContentType(MediaType.TEXT_PLAIN);
		String msg = "Error when getting JWT token: "+ex.getClass().getName()+'-'+ex.getMessage();
		ResponseEntity<String> response = new ResponseEntity<String>(msg, respHeaders, HttpStatus.SERVICE_UNAVAILABLE);
		return response;
    }
}
