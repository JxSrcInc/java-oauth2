package jxsource.net.proxy;

import javax.net.ServerSocketFactory;
import javax.net.SocketFactory;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocketFactory;

/*
 * It is a global service register 
 */
public class AppContext {
	private static AppContext me;
//	private String classLogProcess = "jxsource.net.proxy.LogProcessHttp";
	private LogProcess logProcess;;
	private boolean tlsOutGoingSocket = false;
	private boolean tlsInCommingServerSocket = false;
	private AppContext() {}
	
	public static AppContext get() {
		if(me == null) {
			me = new AppContext();
		}
		return me;
	}
	
	public AppContext setLogProcess(String classLogProcess) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		logProcess = (LogProcess) Class.forName(classLogProcess).newInstance();
		return this;
	}
	
	public LogProcess getLogProcess() {
		return logProcess;
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
		System.err.println(this.getClass().getSimpleName()+": "+sf.getClass().getSimpleName());		
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
		System.err.println(this.getClass().getSimpleName()+": "+ssf.getClass().getSimpleName());
		return ssf;
	}

}