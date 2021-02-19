package jxsource.net.proxy;

import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jxsource.net.proxy.util.ThreadUtil;

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
		LogPipe logServerToClient = new LogPipe(inServer);
		ThreadUtil.createThread(logClientToServer).start();
		ThreadUtil.createThread(logServerToClient).start();
		log.debug(debugInfo("ClientToServer and ServerToClient logs started"));
	}
}
