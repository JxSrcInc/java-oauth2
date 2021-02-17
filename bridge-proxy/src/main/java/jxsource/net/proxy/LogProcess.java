package jxsource.net.proxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PushbackInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jxsource.net.proxy.http.HttpHeaderReader;
import jxsource.net.proxy.util.exception.HttpHeaderReaderException;
import jxsource.net.proxy.util.exception.HttpMessageProcessor;

/*
 * Worker will setup two input streams
 * Derived class must implement run to specify how to use two input streams 
 */
public abstract class LogProcess implements Runnable{
	private static Logger log = LoggerFactory.getLogger(LogPipe.class);
	protected InputStream inClient;
	protected InputStream inServer;
	
	protected void init(InputStream inClient, InputStream inServer) {
		this.inClient = inClient;
		this.inServer = inServer;
	}

	public abstract void run();
	protected String debugLog(String info) {
		String msg = String.format("*** Thread(%s): %s(%d) %s",
				Thread.currentThread().getName(), this.getClass().getSimpleName(), this.hashCode(), info);
		return msg;	
	}
}
