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
 * print client-to-server and server-to-client message in two threads
 */
public class LogProcessPrint extends LogProcess {
	private static Logger log = LoggerFactory.getLogger(LogProcessPrint.class);
	protected InputStream inClient;
	protected InputStream inServer;
	
	public LogProcessPrint() {
		
	}
	protected void init(InputStream inClient, InputStream inServer) {
		this.inClient = inClient;
		this.inServer = inServer;
	}

	@Override
	public void run() {
		LogPipe logClientToServer = new LogPipe(inClient, System.err);
		new Thread(logClientToServer).start();
		LogPipe logServerToClient = new LogPipe(inServer);
		new Thread(logServerToClient).start();
		log.debug(debugLog("ClientToServer and ServerToClient logs started"));
	}
}
