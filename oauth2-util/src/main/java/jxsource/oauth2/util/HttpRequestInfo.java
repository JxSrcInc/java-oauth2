package jxsource.oauth2.util;

import java.net.URI;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;

public class HttpRequestInfo {
	private URI uri;
	private HttpHeaders headers;
	private HttpMethod method;
	
	public static HttpRequestInfoBuilder getBuilder() {
		return new HttpRequestInfoBuilder();
	}
	public static class HttpRequestInfoBuilder {
		private HttpRequest request;
		public HttpRequestInfoBuilder setRequest(HttpRequest request) {
			this.request = request;
			return this;
		}
		public HttpRequestInfo build() {
			HttpRequestInfo info = new HttpRequestInfo();
			info.uri = request.getURI();
			info.headers = request.getHeaders();
			info.method = request.getMethod();
			return info;
		}
	}
	@Override
	public String toString() {
		return "HttpRequestInfo [method=" + method + ", uri=" + uri + ", headers=" + headers + "]";
	}
	
}
