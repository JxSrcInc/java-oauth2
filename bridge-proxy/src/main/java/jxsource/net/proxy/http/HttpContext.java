package jxsource.net.proxy.http;

public class HttpContext {
	private String remoteHost;
	private int remotePort;
	private HttpEditor editor;
	private RequestLog requestLog;
	private ResponseLog responseLog;

	public RequestLog getRequestLog() {
		return requestLog;
	}

	public void setRequestLog(RequestLog requestLog) {
		this.requestLog = requestLog;
	}

	public ResponseLog getResponseLog() {
		return responseLog;
	}

	public void setResponseLog(ResponseLog responseLog) {
		this.responseLog = responseLog;
	}

	public HttpContext setRemoteHost(String host) {
		remoteHost = host;
		return this;
	}

	public String getRemoteHost() {
		return remoteHost;
	}

	public HttpContext setRemotePort(int port) {
		remotePort = port;
		return this;
	}

	public int getRemotePort() {
		return remotePort;
	}	

	public static HttpContext build(String host, int port) {
		return new HttpContext().setRemoteHost(host).setRemotePort(port);
	}

	public HttpEditor getEditor() {
		return editor;
	}

	public HttpContext setEditor(HttpEditor editor) {
		this.editor = editor;
		editor.setHttpContext(this);
		return this;
	}
	
	
}
