package jxsource.net.proxy;

import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jxsource.net.proxy.http.HttpHeaderReader;
import jxsource.net.proxy.util.exception.HttpHeaderReaderException;
import jxsource.net.proxy.util.exception.HttpMessageProcessor;

public class LogClientToServerImpl extends LogPipe{
	private static Logger log = LoggerFactory.getLogger(LogClientToServerImpl.class);

	public LogClientToServerImpl(InputStream in) {
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
				System.err.println(new String(buf));
				proc.init(buf);
				System.err.println("**** LogClientToServer");
			}
		} catch(HttpHeaderReaderException e)
		{
			buf = reader.getHeaderBytes();
		}
		// It may be not HTTP call
		// TODO: need refine
		super.proc();
	}
	
}
