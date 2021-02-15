package jxsource.oauth2.resourceServer.controller;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;

import jxsource.oauth2.util.HttpServletRequestJsonNodeBuilder;

@RestController
@RequestMapping(value = "/spring-security-oauth-resource")
public class FooController {
	protected final Log logger = LogFactory.getLog(getClass());
	
    @RolesAllowed({"ROLE_USER","DISDEV.9ACCS9ADMIN","DISVAL.9ACCS9ADMIN","DISINT.9ACCS9ADMIN","DISPRD.9ACCS9ADMIN","DISDEV.9ACCS99USER","DISVAL.9ACCS99USER","DISINT.9ACCS99USER","DISPRD.9ACCS99USER"})
    @RequestMapping(value = "/foos/{id}", method = RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
    public String getavailableresponse(
            @PathVariable("id") final String id,
            HttpServletRequest request) throws Exception
    {
    	JsonNode info = new HttpServletRequestJsonNodeBuilder()
    			.setRequest(request).loadContent().build();
//    	String json = info.toString();
    	String json = "{\"id\":93,\"name\":\"HGJv\"}";
		logger.debug("** response: "+json);
		return json;
	}

	@ExceptionHandler(Exception.class) 
	   public ResponseEntity<String> exceptionHandler(Exception ex) { 
			logger.error("Controller Error", ex);
			ResponseEntity<String> responseEntity = new ResponseEntity<String>("Controller Error", HttpStatus.INTERNAL_SERVER_ERROR);
			return responseEntity;
		}

}
