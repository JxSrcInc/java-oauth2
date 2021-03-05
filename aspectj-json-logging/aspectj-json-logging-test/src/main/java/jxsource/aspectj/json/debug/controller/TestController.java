package jxsource.aspectj.json.debug.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;

import jxsource.aspectj.json.debug.ThreadLog;
import jxsource.aspectj.json.debug.ThreadLogLocal;
import jxsource.aspectj.json.debug.model.GlobalError;
import jxsource.aspectj.json.debug.model.User;
import jxsource.aspectj.json.debug.model.UserList;

@RestController
public class TestController {
	@Autowired
	UserList list;
	
	@RequestMapping(value = "/user/{username}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public User  getUser(@PathVariable("username") final String username) {
		User user = list.get(username);
		if(user != null) {
			return user;
		} else {
			throw new RuntimeException("invalid user name: "+username);
		}
	}
	
    @ExceptionHandler(Exception.class)
    public ResponseEntity<GlobalError> exceptionHandler(Exception ex)
    {
    	GlobalError err = new GlobalError().setException(ex);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        return new ResponseEntity<GlobalError>(err, headers, HttpStatus.BAD_REQUEST);
    }

}
