package jxsource.oauth2.util;

import java.net.URI;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

public class HttpResponseInfo {
	private HttpHeaders headers;
	
	public static HttpRequestInfoBuilder getBuilder() {
		return new HttpRequestInfoBuilder();
	}
	public static class HttpRequestInfoBuilder {
		private ResponseEntity response;
		public HttpRequestInfoBuilder setRequest(ResponseEntity response) {
			this.response = response;
			return this;
		}
		public HttpResponseInfo build() {
			HttpResponseInfo info = new HttpResponseInfo();
			info.headers = response.getHeaders();
			return info;
		}
	}
	
}
