package jxsource.net.proxy;

import javax.net.ServerSocketFactory;
import javax.net.SocketFactory;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocketFactory;

import jxsource.net.proxy.tcp.Log;

/*
 * It is a global service register 
 */
public class AppContext {
	private static AppContext me;
//	private String classLogProcess = "jxsource.net.proxy.LogProcessHttp";
	private Log log;
	private boolean tlsOutGoingSocket = false;
	private boolean tlsInCommingServerSocket = false;
	
	private String appType;
	private String connType;
	
	private boolean tcpLog = false;
	private String remoteDomain;
	private int remotePort;
	private int serverSocketPort;

	
	private AppContext() {}
	
	public int getServerSocketPort() {
		return serverSocketPort;
	}

	public AppContext setServerSocketPort(int serverSocketPort) {
		this.serverSocketPort = serverSocketPort;
		return this;
	}

	public String getRemoteDomain() {
		return remoteDomain;
	}

	public AppContext setRemoteDomain(String remoteDomain) {
		this.remoteDomain = remoteDomain;
		return  this;
	}

	public int getRemotePort() {
		return remotePort;
	}

	public AppContext setRemotePort(int remotePort) {
		this.remotePort = remotePort;
		return this;
	}

		
	public String getConnType() {
		return connType;
	}

	public AppContext setConnType(String connType) {
		this.connType = connType;
		return this;
	}

	public String getAppType() {
		return appType;
	}

	public AppContext setAppType(String appType) {
		this.appType = appType;
		return this;
	}

	public boolean isTcpLog() {
		return tcpLog;
	}

	public AppContext setTcpLog(boolean httpBodyLog) {
		this.tcpLog = httpBodyLog;
		return this;
	}

	public static AppContext get() {
		if(me == null) {
			me = new AppContext();
		}
		return me;
	}
	
	public AppContext setLog(String classLog) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		log = (Log) Class.forName(classLog).newInstance();
		return this;
	}
	
	public Log getLog() {
		return log;
	}
	
	public AppContext setTlsOutGoingSocket(boolean tls) {
		this.tlsOutGoingSocket = tls;
		return this;
	}
	
	public SocketFactory getSocketFactory() {
		return SocketFactory.getDefault();		
	}
	
	public SocketFactory getSSLSocketFactory() {
		return SSLSocketFactory.getDefault();		
	}

	public SocketFactory getDefaultSocketFactory() {
		SocketFactory sf = null;
		if(tlsOutGoingSocket) {
			sf = getSSLSocketFactory();
		} else {
			sf = getSocketFactory();
		}
		return sf;
	}

	public AppContext setTlsInComingServerSocket(boolean tls) {
		this.tlsInCommingServerSocket = tls;
		return this;
	}
	
	public ServerSocketFactory getServerSocketFactory() {
		return ServerSocketFactory.getDefault();		
	}
	
	public ServerSocketFactory getSSLServerSocketFactory() {
		return SSLServerSocketFactory.getDefault();		
	}

	public ServerSocketFactory getDefaultServerSocketFactory() {
		ServerSocketFactory ssf = null;
		if(tlsInCommingServerSocket) {
			ssf = getSSLServerSocketFactory();
		} else {
			ssf = getServerSocketFactory();
		}
		return ssf;
	}

}
