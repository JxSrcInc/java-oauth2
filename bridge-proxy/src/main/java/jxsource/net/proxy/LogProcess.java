package jxsource.net.proxy;

import java.io.InputStream;

import jxsource.net.proxy.util.ThreadUtil;

/*
 * Worker will setup two input streams
 * Derived class must implement run to specify how to use two input streams 
 */
public abstract class LogProcess implements Runnable{
	protected InputStream inClient;
	protected InputStream inServer;
	
	protected void init(InputStream inClient, InputStream inServer) {
		this.inClient = inClient;
		this.inServer = inServer;
	}

	public abstract void run();
	protected String debugInfo(String info) {
		String msg = String.format("*** %s: %s(%d) %s",
				ThreadUtil.threadInfo(), getClass().getSimpleName(), hashCode(), info);
		return msg;	
	}
}
