package jxsource.net.proxy.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jxsource.net.proxy.Worker;
import jxsource.net.proxy.tcp.Log;
import jxsource.net.proxy.tcp.PipeLocalToRemote;
import jxsource.net.proxy.tcp.PipeRemoteToLocal;
import jxsource.net.proxy.tcp.PipeWorker;
import jxsource.net.proxy.util.ThreadUtil;

public class HttpWorker implements Worker{
	private static Logger logger = LoggerFactory.getLogger(HttpWorker.class);
	private Socket localSocket;
	private Socket remoteSocket;
	private InputStream localInput;
	private OutputStream localOutput;
	private InputStream remoteInput;
	private OutputStream remoteOutput;
	private HttpPipeProcess procRequest = HttpPipeProcess.build();
	private HttpPipeProcess procResponse = HttpPipeProcess.build();

	// if logProcess is null, no threadLogProcess run
	private HttpContext requestContext;
	private HttpContext responseContext;

	public HttpWorker initHttp(String remoteHost, int remotePort, 
			boolean downloadData, String downloadDir, String downloadMime) {
		this.requestContext = HttpContext.build(remoteHost, remotePort).setEditor(new HttpRequestEditor())
				.setDownloadData(downloadData)
				.setDownloadDir(downloadDir)
				.setDownloadMime(downloadMime);
		this.responseContext = HttpContext.build(remoteHost, remotePort).setEditor(new HttpResponseEditor())
				.setDownloadData(downloadData)
				.setDownloadDir(downloadDir)
				.setDownloadMime(downloadMime);
		return this;
	}

	@Override
	public void run() {
		while(true) {
			// request
			try {
			procRequest.proc();
			} catch(Exception e) {
				logger.error(debugInfo("request error"), e);
				break;
			} 
			// response
			try {
			procResponse.proc();
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
		RequestLog requestLog = new RequestLog();
		requestLog.setHttpContext(requestContext);
		ResponseLog responseLog = new ResponseLog().setRequestLog(requestLog);
		responseLog.setHttpContext(responseContext);
		procRequest.init("Request", localInput, remoteOutput, requestLog, requestContext);
		procResponse.init("Response", remoteInput, localOutput, responseLog, responseContext);
		
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
