package jxsource.net.proxy.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jxsource.net.proxy.http.HttpHeaderReader;
import jxsource.net.proxy.http.HttpHeader;

public class HttpLogProcess {
	private static Logger log = LoggerFactory.getLogger(HttpLogProcess.class);
	protected InputStream in;
	protected byte[] headerBytes;
	HttpHeaderReader reader = HttpHeaderReader.build();
	HttpHeader handler = HttpHeader.build();
	byte[] content;
	
	public HttpLogProcess init(InputStream in) {
		this.in = in;
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
			headerBytes = new byte[bodyStart];
			System.arraycopy(buf, 0, headerBytes, 0, bodyStart);
			handler.init(headerBytes);
			String contentLength = handler.getHeaderValue("Content-Length");
			if(contentLength != null) {
				procContentLength(Integer.parseInt(contentLength), in);
			}
		}
	}
	
	public void procContentLength(int length, InputStream in) throws IOException {
		content = new byte[length];
		int pos = 0;
		while(pos<length) {
			int i = in.read(content,pos,length-pos);
			pos += i;
			System.err.println(length+","+pos+","+i);
		}
	}
	
	public byte[] getHeaderBytes() {
		return headerBytes;
	}
	public byte[] getContent() {
		return content;
	}
}
