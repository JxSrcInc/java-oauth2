package jxsource.net.proxy.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jxsource.net.proxy.Log;
import jxsource.net.proxy.PipeLocalToRemote;
import jxsource.net.proxy.PipeRemoteToLocal;
import jxsource.net.proxy.PipeWorker;
import jxsource.net.proxy.Worker;
import jxsource.net.proxy.util.ThreadUtil;

public class HttpWorker implements Worker{
	private static Logger logger = LoggerFactory.getLogger(HttpWorker.class);
	private Socket localSocket;
	private Socket remoteSocket;
	private InputStream localInput;
	private OutputStream localOutput;
	private InputStream remoteInput;
	private OutputStream remoteOutput;
	private HttpPipeProcess procL2R = HttpPipeProcess.build();
	private HttpPipeProcess procR2L = HttpPipeProcess.build();

	// if logProcess is null, no threadLogProcess run
	private Log logL2R;
	private Log logR2L;

	public HttpWorker setLog(Log logL2R, Log logR2L) {
		this.logL2R = logL2R;
		this.logR2L = logR2L;
		return this;
	}

	@Override
	public void run() {
		while(true) {
			// request
			try {
			procL2R.proc();
			} catch(Exception e) {
				logger.error(debugInfo("request error"), e);
				break;
			} 
			// response
			try {
			procR2L.proc();
			} catch(Exception e) {
				logger.error(debugInfo("response error"), e);
				break;
			} 
		}
		closeInputStream(localInput);
		closeOutputStream(localOutput);
		closeSocket(localSocket);
		closeInputStream(remoteInput);
		closeOutputStream(remoteOutput);
		closeSocket(remoteSocket);
		logger.debug(debugInfo("HttpWorker stop"));
	}

	@Override
	public void init(Socket localSocket, InputStream localSocketInput, Socket remoteSocket) throws Exception {
		this.localSocket = localSocket;
		this.remoteSocket = remoteSocket;
		logger.debug(debugInfo("starat"));
		if (localSocketInput != null) {
			this.localInput = localSocketInput;
		} else {
			this.localInput = localSocket.getInputStream();
		}
		localOutput = this.localSocket.getOutputStream();
		remoteInput = this.remoteSocket.getInputStream();
		remoteOutput = this.remoteSocket.getOutputStream();
		procL2R.init("L2R", localInput, remoteOutput, logL2R, null);
		procR2L.init("R2L", remoteInput, localOutput, logR2L, null);
		
	}

	private String debugInfo(String msg) {
		String info = String.format("\n\t*** %s: Worker(%d), remoteSocket(%s:%d): %s", ThreadUtil.threadInfo(),
				this.hashCode(), 
				remoteSocket.getInetAddress().getHostName(), remoteSocket.getPort(), msg);
		return info;

	}

	private void closeSocket(Socket socket) {
		try {
			socket.close();
		} catch (IOException e) {
		}
	}
	private void closeInputStream(InputStream in) {
	try {
		in.close();
	} catch (IOException e1) {
	}
	}
	private void closeOutputStream(OutputStream out) {
	try {
		out.close();
	} catch (IOException e1) {
	}
	}
}
