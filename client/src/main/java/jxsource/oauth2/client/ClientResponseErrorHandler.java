package jxsource.oauth2.client;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;

import jxsource.oauth2.util.HttpUtil;

public class ClientResponseErrorHandler extends DefaultResponseErrorHandler{
	private static Logger logger = LoggerFactory.getLogger(ClientResponseErrorHandler.class);
	
	@Override
	public void handleError(ClientHttpResponse response)
            throws IOException {
		System.out.println(response);
		logger.error(Thread.currentThread().getName()+": "+HttpUtil.getResponseInfo(response));
		super.handleError(response);
	}
}
