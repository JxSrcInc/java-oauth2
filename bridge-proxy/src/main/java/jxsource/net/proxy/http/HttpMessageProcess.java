package jxsource.net.proxy.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jxsource.net.proxy.http.HttpHeaderReader;
import jxsource.net.proxy.exception.HttpHeaderReaderException;
import jxsource.net.proxy.http.HttpHeader;

public class HttpMessageProcess {
	private static Logger log = LoggerFactory.getLogger(HttpMessageProcess.class);
	protected InputStream in;
	protected byte[] headerBytes;
	HttpHeaderReader reader = HttpHeaderReader.build();
	HttpHeader handler = HttpHeader.build();
	byte[] content;
	public HttpMessageProcess init(InputStream in) {
		this.in = in;
		return this;
	}

	public void proc() throws IOException, HttpHeaderReaderException{
		content = null;
		headerBytes = reader.getHeaderBytes(in);
				handler.init(headerBytes);
				String contentLength = handler.getHeaderValue("Content-Length");
				if(contentLength != null) {
					getContent(Integer.parseInt(contentLength), in);
				}
	}
	
	public void getContent(int length, InputStream in) throws IOException {
		content = new byte[length];
		int pos = 0;
		while(pos<length) {
			int i = in.read(content,pos,length-pos);
			pos += i;
		}
	}
	
	public byte[] getHeaderBytes() {
		return headerBytes;
	}
	public byte[] getContent() {
		return content;
	}
}
