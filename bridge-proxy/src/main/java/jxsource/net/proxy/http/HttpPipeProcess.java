package jxsource.net.proxy.http;

import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jxsource.net.proxy.Log;
import jxsource.net.proxy.Pipe;
import jxsource.net.proxy.util.ThreadUtil;

/*
 * Base class to handle each Pipe for Http.
 */
public class HttpPipeProcess {
//	private Logger logger = LoggerFactory.getLogger(HttpPipeProcess.class);
	private InputStream in;
	private OutputStream out;
	private Log appLog;
	private HttpHeaderReader reader = HttpHeaderReader.build();
	private HttpHeader handler = HttpHeader.build();
	private String name;
	private HttpPipeContext context;
	
	public static HttpPipeProcess build() {
		return new HttpPipeProcess();
	}

	public HttpPipeProcess init(String name, InputStream in, OutputStream out, Log appLog, HttpPipeContext context) {
		this.in = in;
		this.out = out;
		// logOut may be null if no output requires
		this.appLog = appLog;
		this.name = name;
		this.context = context;
		return this;
	}

	public void proc() throws IOException {
		// buf must be large enough to contain all HTTP headers
		byte[] buf = new byte[1024 * 8];
		int i = 0;
		int bodyStart = 0;
		while (true) {
			try {
				i = in.read(buf, bodyStart, buf.length - bodyStart);
			} catch (Exception ioe) {
				throw new IOException("Input stream error", ioe);
			}
			if (i != -1) {
				bodyStart += i;
				while (reader.getHeaderBytes(buf, bodyStart) < 0)
					;
				byte[] headerBytes = new byte[bodyStart];
				System.arraycopy(buf, 0, headerBytes, 0, bodyStart);
				handler.init(headerBytes);
				// TODO: modify header
				headerBytes = context.getHttpHeaderEditor().edit(headerBytes);
				try {
					output(headerBytes);
				} catch (Exception e) {
					throw new IOException(name + " output Http header error", e);
				}
				String contentLength = handler.getHeaderValue("Content-Length");
				if (contentLength != null) {
					procContentLength(Integer.parseInt(contentLength), in);
				}
			}
		}
	}

	public void procContentLength(int length, InputStream in) throws IOException {
		byte[] content = new byte[length];
		int pos = 0;
		while (pos < length) {
			try {
				int i = in.read(content, pos, length - pos);
				pos += i;
			} catch (Exception ioe) {
				throw new IOException("Output stream error", ioe);
			}
		}
		try {
			output(content);
		} catch (Exception e) {
			throw new IOException(name + " output Http body error", e);
		}
	}

	private void output(byte[] data) throws IOException {
		out.write(data);
		out.flush();
		appLog.logPipe(name, data);
	}

	protected String getLogMsg(String info) {
		String msg = String.format("*** %s: %s(%d) %s", ThreadUtil.threadInfo(), name, this.hashCode(), info);
		return msg;

	}

}
