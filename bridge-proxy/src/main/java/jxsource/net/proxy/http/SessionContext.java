package jxsource.net.proxy.http;

import java.util.HashMap;
import java.util.Map;

public class SessionContext {
	private String remoteHost;
	private int remotePort;
	private Map<String, Object> attributes = new HashMap<>();

	private String downloadDir;
	private boolean downloadData;
	// comma separated string
	private String downloadMime;

	public String getDownloadMime() {
		return downloadMime;
	}

	// comma separated string
	public SessionContext setDownloadMime(String downloadMime) {
		this.downloadMime = downloadMime;
		return this;
	}

	public String getDownloadDir() {
		return downloadDir;
	}

	public SessionContext setDownloadDir(String downloadDir) {
		this.downloadDir = downloadDir;
		return this;
	}

	public boolean isDownloadData() {
		return downloadData;
	}

	public SessionContext setDownloadData(boolean downloadData) {
		this.downloadData = downloadData;
		return this;
	}

	public SessionContext setRemoteHost(String host) {
		remoteHost = host;
		return this;
	}

	public String getRemoteHost() {
		return remoteHost;
	}

	public SessionContext setRemotePort(int port) {
		remotePort = port;
		return this;
	}

	public int getRemotePort() {
		return remotePort;
	}	

	public static SessionContext build(String host, int port) {
		return new SessionContext().setRemoteHost(host).setRemotePort(port);
	}

	public SessionContext addAttribute(String name, Object value) {
		attributes.put(name, value);
		return this;
	}

	public SessionContext removeAttribute(String name) {
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
