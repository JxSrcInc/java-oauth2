package jxsource.net.proxy;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import jxsource.net.proxy.http.HttpContext;
import jxsource.net.proxy.http.HttpWorker;
import jxsource.net.proxy.util.ThreadUtil;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpParser;
import javax.net.SocketFactory;

public class Dispatcher implements Runnable {
	private static Logger log = LoggerFactory.getLogger(Dispatcher.class);
	private Worker worker;
	private String appType;
	private String remoteDomain;
	private int remotePort;

	private int socketTimeout = 3;
	private volatile int count = 0;

	private Socket localSocket;
	private InputStream localSocketInput;
	private AppContext appContext = AppContext.get();

	public Dispatcher init(Socket localSocket, String appType, String remoteDomain, int remotePort) {
		this.localSocket = localSocket;
		this.appType = appType;
		this.remoteDomain = remoteDomain;
		this.remotePort = remotePort;
		return this;
	}

	private Socket getRemoteSocket() throws UnknownHostException {
		InetAddress addr = InetAddress.getByName(remoteDomain);
		SocketAddress sockaddr = new InetSocketAddress(addr, remotePort);
		while (count < 3) {
			int timeout = (socketTimeout * (count++)) * 1000;
			try {
				log.debug(
						logMsg(String.format("connect to %s:%d with timeout %d sec", remoteDomain, remotePort, timeout)));
				Socket s = appContext.getDefaultSocketFactory().createSocket();
				s.setSoTimeout(timeout);
				s.connect(sockaddr);
				return s;
			} catch (IOException ioe) {
				ioe.printStackTrace();
				String msg = String.format("Re-connect(%d) to %s(%d) with timeout %d", count, remoteDomain, remotePort, timeout);
				System.err.println(logMsg(msg));
			}
		}
		return null;

	}

	private void prepare() throws IOException {
		switch(appType) {
		case Constants.AppBridgeType:
			localSocketInput = localSocket.getInputStream();
			break;
		case Constants.AppProxyType:
			prepareHostFromRequest();
			break;
			default:
				throw new IOException("Invalid appType: "+appType);
		}
	}

	private void prepareHostFromRequest() throws IOException {
		// size must be large enough to contain all headers
		int size = 1024 * 8;
		PushbackInputStream clientInput = new PushbackInputStream(localSocket.getInputStream(), size);
		byte[] buf = new byte[size];
		byte b13 = 13;
		byte b10 = 10;
		int count = 0;
		for (count = 0; count < size; count++) {
			buf[count] = (byte) clientInput.read();
			// check end HTTP headers
			// the original input stream (client.getInputStream() cannot be consumed
			// completely
			// it must have at least one byte.
			// otherwise unread() method will not work.
			// look the re-read from original input stream is block.
			// so check of end headers is based on [13][10][13] not [13][10][13][10]
			// which keeps the last headers byte [10] in the original input stream
			if (count > 4 && buf[count] == b13 && buf[count - 1] == b10 && buf[count - 2] == b13) {
				break;
			}
		}
		// push back
		for (int i = count; i >= 0; i--) {
			clientInput.unread(buf[i]);
		}
		// let buf be a complete headers
		buf[count] = b10;
		// TODO: parse HTTP headers
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(buf, 0, count + 1);
		String line = HttpParser.readLine(byteArrayInputStream, Charset.defaultCharset().name());
		Header[] headers = HttpParser.parseHeaders(byteArrayInputStream, Charset.defaultCharset().name());
		String host = null;
		int port = 0;
		for (Header header : headers) {
			if (header.getName().equals("Host")) {
				String value = header.getValue();
				int index = value.indexOf(":");
				if (index == -1) {
					host = value;
					port = 80;
				} else {
					host = value.substring(0, index);
					port = Integer.parseInt(value.substring(index + 1));
				}
				break;
			}
		}
		if (host == null) {
			throw new IOException("Cannot find host and port for remote socket.");
		}
		remoteDomain = host;
		remotePort = port;
	}

	@Override
	public void run() {
		log.debug(logMsg("start Dispatcher"));
		try {
			prepare();
			dispatch();
		} catch (Exception e) {
			log.error(logMsg("Fail to start Dispatcher thread"), e);
			try {
				localSocket.close();
			} catch (IOException e1) {
			}
		}
	}

	private void dispatch() throws Exception {
		Socket remoteSocket = getRemoteSocket();
		if (remoteSocket == null) {
			throw new IOException();
		}
		worker = WorkerFactory.build().create(remoteDomain, remotePort);
		if(worker instanceof HttpWorker) {
			((HttpWorker)worker).initHttp(remoteDomain, remotePort,
					appContext.isDownloadData(), appContext.getDownloadDir(),
					appContext.getDownloadMime());
		}
		worker.init(localSocket, localSocketInput, remoteSocket);
		ThreadUtil.createThread(worker).start();
		log.debug(logMsg("end Dispatcher with starting Worker"));
	}

	private String logMsg(String info) {
		String msg = String.format("\n\t*** %s: Dispatcher(%d)-client(%s:%d): %s", ThreadUtil.threadInfo(),
				this.hashCode(), localSocket.getInetAddress().getHostName(), localSocket.getPort(), info);
		return msg;

	}

}
