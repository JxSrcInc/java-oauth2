package jxsource.net.proxy;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.net.Socket;
import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import jxsource.net.proxy.util.ThreadUtil;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpParser;

//@Component
//@Scope("prototype")
public class Dispatcher implements Runnable {
	private static Logger log = LoggerFactory.getLogger(Dispatcher.class);
//	@Autowired
	private Worker worker = new Worker();
//	@Value("${proxy.bridge:false}")
	private boolean bridge;
//	@Value("${proxy.server.host}")
	private String serverHost;
//	@Value("${proxy.server.port}")
	private int serverPort;

	private Socket client;
	private PushbackInputStream clientInput;

	public Dispatcher init(Socket client, boolean bridge, String serverHost, int serverPort) {
		this.client = client;
		this.bridge = bridge;
		this.serverHost = serverHost;
		this.serverPort = serverPort;
		return this;
	}

	private Socket getServerSocket() throws IOException {
		if(bridge) {
			clientInput = new PushbackInputStream(client.getInputStream());
			log.debug(logMsg(String.format("connect to %s:%d", serverHost, serverPort)));
			return new Socket(serverHost, serverPort);
		} else {
			return byProxy();
		}
	}
	private Socket byProxy() throws IOException{
		// size must be large enough to contain all headers
		int size = 1024 * 8;
		clientInput = new PushbackInputStream(client.getInputStream(), size);
		byte[] buf = new byte[size];
		byte b13 = 13;
		byte b10 = 10;
		int count = 0;
		for (count = 0; count < size; count++) {
			buf[count] = (byte) clientInput.read();
			// check end HTTP headers
			// the original input stream (client.getInputStream() cannot be consumed completely
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
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(buf,0,count+1);
		String line = HttpParser.readLine(byteArrayInputStream,Charset.defaultCharset().name());
		Header[] headers = HttpParser.parseHeaders(byteArrayInputStream,Charset.defaultCharset().name());
		String host = null;
		int port = 0;
		for(Header header: headers) {
			if(header.getName().equals("Host")) {
				String value = header.getValue();
				int index = value.indexOf(":");
				if(index == -1) {
					host = value;
					port = 80;
				} else {
					host = value.substring(0,index);
					port = Integer.parseInt(value.substring(index+1));
				}
				break;
			}
		}
		if(host == null) {
			throw new IOException("Cannot find host and port for server.");
		}
		return new Socket(host, port);
	}

	@Override
	public void run() {
		log.debug(logMsg("start Dispatcher"));
		try {
			Socket server = getServerSocket();
			worker.init(client, clientInput, server);
			ThreadUtil.createThread(worker).start();
			log.debug(logMsg("end Dispatcher with starting Worker"));
		} catch (Exception e) {
			log.error(logMsg("Fail to start Dispatcher thread"), e);
			try {
				client.close();
			} catch (IOException e1) {
			}
		}
	}

	private String logMsg(String info) {
		String msg = String.format("*** %s: Dispatcher(%d)-client(%s:%d): %s", ThreadUtil.threadInfo(),
				this.hashCode(), client.getInetAddress().getHostName(), client.getPort(), info);
		return msg;

	}
}
