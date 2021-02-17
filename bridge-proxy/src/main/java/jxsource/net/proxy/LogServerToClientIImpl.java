package jxsource.net.proxy;

import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jxsource.net.proxy.http.HttpHeaderReader;
import jxsource.net.proxy.util.exception.HttpHeaderReaderException;
import jxsource.net.proxy.util.exception.HttpMessageProcessor;

public class LogServerToClientIImpl extends LogPipe{
	private static Logger log = LoggerFactory.getLogger(LogServerToClientIImpl.class);

	public LogServerToClientIImpl(InputStream in) {
		super(in);
	}

	@Override
	protected void proc() throws Exception {
		if(in == null) return;
		byte[] buf = new byte[1024*8];
		HttpHeaderReader reader = HttpHeaderReader.build();
		HttpMessageProcessor proc = HttpMessageProcessor.build();
		try {
			while(true) {
				buf = reader.getHeaderBytes(in);
				System.out.println(new String(buf));
				proc.init(buf);
				String contentLength = proc.getHeaderValue("Content-Length");
				if(contentLength != null) {
					getContent(Integer.parseInt(contentLength), in);
				}
				System.out.println("**** LogServerToClient");
			}
		} catch(HttpHeaderReaderException e)
		{
			buf = reader.getHeaderBytes();
		}
		// It may be not HTTP call
		// TODO: need refine
		super.proc();
	}
	
	public void getContent(int length, InputStream in) throws IOException {
		byte[] buf = new byte[length];
		int pos = 0;
		while(pos<length) {
			int i = in.read(buf,pos,length-pos);
			pos += i;
		}
		System.out.println(new String(buf));
	}
}
