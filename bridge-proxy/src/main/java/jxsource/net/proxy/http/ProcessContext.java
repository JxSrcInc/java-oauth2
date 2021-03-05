package jxsource.net.proxy.http;

import java.util.HashMap;
import java.util.Map;

/*
 * Context used by RequestProcess and ResponseProcess separately
 * Use SessionContext to get common information and pass information between them
 */
public class ProcessContext {
//	private String remoteHost;
//	private int remotePort;
	private Map<String, Object> attributes = new HashMap<>();
	private SessionContext sessionContext;

//	private String downloadDir;
//	private boolean downloadData;
//	// comma separated string
//	private String downloadMime;

	
	public SessionContext getSessionContext() {
		return sessionContext;
	}

	public ProcessContext setSessionContext(SessionContext sessionContext) {
		this.sessionContext = sessionContext;
		return this;
	}

//	public String getDownloadMime() {
//		return downloadMime;
//	}
//
//	// comma separated string
//	public ProcessContext setDownloadMime(String downloadMime) {
//		this.downloadMime = downloadMime;
//		return this;
//	}
//
//	public String getDownloadDir() {
//		return downloadDir;
//	}
//
//	public ProcessContext setDownloadDir(String downloadDir) {
//		this.downloadDir = downloadDir;
//		return this;
//	}
//
//	public boolean isDownloadData() {
//		return downloadData;
//	}
//
//	public ProcessContext setDownloadData(boolean downloadData) {
//		this.downloadData = downloadData;
//		return this;
//	}
//
//	public RequestLog getRequestLog() {
//		return requestLog;
//	}
//
//	public void setRequestLog(RequestLog requestLog) {
//		this.requestLog = requestLog;
//	}
//
//	public ResponseLog getResponseLog() {
//		return responseLog;
//	}
//
//	public void setResponseLog(ResponseLog responseLog) {
//		this.responseLog = responseLog;
//	}

//	public ProcessContext setRemoteHost(String host) {
//		remoteHost = host;
//		return this;
//	}
//
//	public String getRemoteHost() {
//		return remoteHost;
//	}
//
//	public ProcessContext setRemotePort(int port) {
//		remotePort = port;
//		return this;
//	}
//
//	public int getRemotePort() {
//		return remotePort;
//	}	

//	public HttpEditor getEditor() {
//		return editor;
//	}
//
//	public ProcessContext setEditor(HttpEditor editor) {
//		this.editor = editor;
//		editor.setHttpContext(this);
//		return this;
//	}
	
	public ProcessContext addAttribute(String name, Object value) {
		attributes.put(name, value);
		return this;
	}

	public ProcessContext removeAttribute(String name) {
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
