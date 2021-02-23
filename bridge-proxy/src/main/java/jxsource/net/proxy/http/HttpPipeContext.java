package jxsource.net.proxy.http;

public class HttpPipeContext {
	private String remoteHost;
	private int remotePort;
	private String localHost;
	private int localPort;
	private HttpEditor editor;
	
	public HttpPipeContext setLocalHost(String host) {
		localHost = host;
		return this;
	}

	public String getLocalHost() {
		return localHost;
	}

	public HttpPipeContext setLocalPort(int port) {
		localPort = port;
		return this;
	}

	public int getLocalPort() {
		return localPort;
	}

	public HttpPipeContext setRemoteHost(String host) {
		remoteHost = host;
		return this;
	}

	public String getRemoteHost() {
		return remoteHost;
	}

	public HttpPipeContext setRemotePort(int port) {
		remotePort = port;
		return this;
	}

	public int getLRemotePort() {
		return remotePort;
	}

	public HttpPipeContext setHttpHeaderEditor(HttpEditor editor) {
		this.editor = editor;
		return this;
	}

	public HttpEditor getHttpHeaderEditor() {
		// TODO Auto-generated method stub
		return editor;
	}


}
