package jxsource.net.proxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jxsource.net.proxy.http.HttpHeaderReader;
import jxsource.net.proxy.util.exception.HttpHeaderReaderException;
import jxsource.net.proxy.util.exception.HttpMessageProcessor;

public class HttpMessageProcess {
	private static Logger log = LoggerFactory.getLogger(HttpMessageProcess.class);
	private PrintStream ps;
	protected InputStream in;
	public HttpMessageProcess init(InputStream in, PrintStream...ps) {
		this.in = in;
		if(ps.length > 0) {
			this.ps = ps[0];
		} else {
			this.ps = System.out;
		}
		return this;
	}

	protected void proc() throws Exception {
		byte[] buf = new byte[1024*8];
		HttpHeaderReader reader = HttpHeaderReader.build();
		HttpMessageProcessor proc = HttpMessageProcessor.build();
				buf = reader.getHeaderBytes(in);
				ps.println(new String(buf));
				proc.init(buf);
				String contentLength = proc.getHeaderValue("Content-Length");
				if(contentLength != null) {
					getContent(Integer.parseInt(contentLength), in);
				}
				ps.println("**** LogServerToClient");
	}
	
	public void getContent(int length, InputStream in) throws IOException {
		byte[] buf = new byte[length];
		int pos = 0;
		while(pos<length) {
			int i = in.read(buf,pos,length-pos);
			pos += i;
		}
		ps.println(new String(buf));
	}
}
