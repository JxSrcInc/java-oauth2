package jxsource.net.proxy.http;

public class HttpContext {
	private String remoteDomain;
	private int remotePort;
	private HttpEditor editor;

	public HttpContext setRemoteDomain(String domain) {
		remoteDomain = domain;
		return this;
	}

	public String getRemoteDomain() {
		return remoteDomain;
	}

	public HttpContext setRemotePort(int port) {
		remotePort = port;
		return this;
	}

	public int getRemotePort() {
		return remotePort;
	}	

	public static HttpContext build(String domain, int port) {
		return new HttpContext().setRemoteDomain(domain).setRemotePort(port);
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
