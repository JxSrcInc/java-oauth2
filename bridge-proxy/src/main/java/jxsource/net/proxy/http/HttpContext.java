package jxsource.net.proxy.http;

import java.util.HashMap;
import java.util.Map;

public class HttpContext {
	private String remoteHost;
	private int remotePort;
	private HttpEditor editor;
	private RequestLog requestLog;
	private ResponseLog responseLog;
	private Map<String, Object> attributes = new HashMap<>();

	private String downloadDir;
	private boolean downloadData;
	// comma separated string
	private String downloadMime;

	public String getDownloadMime() {
		return downloadMime;
	}

	// comma separated string
	public HttpContext setDownloadMime(String downloadMime) {
		this.downloadMime = downloadMime;
		return this;
	}

	public String getDownloadDir() {
		return downloadDir;
	}

	public HttpContext setDownloadDir(String downloadDir) {
		this.downloadDir = downloadDir;
		return this;
	}

	public boolean isDownloadData() {
		return downloadData;
	}

	public HttpContext setDownloadData(boolean downloadData) {
		this.downloadData = downloadData;
		return this;
	}

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
	
	public HttpContext addAttribute(String name, Object value) {
		attributes.put(name, value);
		return this;
	}

	public HttpContext removeAttribute(String name) {
		attributes.remove(name);
		return this;
	}
	public Object getAttribute(String name) {
		return attributes.get(name);
	}

	public String getValue(String name) {
		Object val = attributes.get(name);
		return val==null?null:val.toString();
	}
	
}
