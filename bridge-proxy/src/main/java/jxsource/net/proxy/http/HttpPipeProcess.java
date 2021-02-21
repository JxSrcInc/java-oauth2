package jxsource.net.proxy.http;

import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jxsource.net.proxy.Pipe;
import jxsource.net.proxy.util.ThreadUtil;

/*
 * Base class to handle each Pipe for Http.
 */
public class HttpPipeProcess {
	private Logger log = LoggerFactory.getLogger(HttpPipeProcess.class);
	private InputStream in;
	private OutputStream out;
	private OutputStream logOut;
	private HttpHeaderReader reader = HttpHeaderReader.build();
	private HttpHeader handler = HttpHeader.build();
	private String name;
	private boolean logOutReady = true;
	
	public static HttpPipeProcess build() {
		return new HttpPipeProcess();
	}
	public HttpPipeProcess init(String name, InputStream in, OutputStream out, OutputStream logOut) {
		this.in = in;
		this.out = out;
		// logOut may be null if no output requires 
		this.logOut = logOut;
		this.name = name;
		return this;
	}
	
	public void proc() throws IOException{
		// buf must be large enough to contain all HTTP headers
		byte[] buf = new byte[1024*8];
		int i = 0;
		int bodyStart = 0;
		while ((i = in.read(buf, bodyStart, buf.length-bodyStart)) != -1) {
			bodyStart += i;
			while(reader.getHeaderBytes(buf, bodyStart) < 0);
			output(buf, 0, bodyStart);
			byte[] headerBytes = new byte[bodyStart];
			System.arraycopy(buf, 0, headerBytes, 0, bodyStart);
			handler.init(headerBytes);
			String contentLength = handler.getHeaderValue("Content-Length");
			if(contentLength != null) {
				procContentLength(Integer.parseInt(contentLength), in);
			}
		}
	}
	
	public void procContentLength(int length, InputStream in) throws IOException {
		byte[] content = new byte[length];
		int pos = 0;
		while(pos<length) {
			int i = in.read(content,pos,length-pos);
			pos += i;
		}
		output(content, 0,length);
	}
	
	private void output(byte[] data, int offset, int length) throws IOException{
//		System.out.print(new String(data, offset, length));
		out.write(data, offset, length);
		out.flush();
		if (logOutReady && logOut != null) {
			try {
				logOut.write(data);
				logOut.flush();
			} catch (IOException e) {
				// turn logOut off
				logOutReady = false;
				log.error(getLogMsg("logOut Exception"), e);
			}
		}
	}

	protected String getLogMsg(String info) {
		String msg = String.format("*** %s: %s(%d) %s", ThreadUtil.threadInfo(), name, this.hashCode(), info);
		return msg;

	}

}
