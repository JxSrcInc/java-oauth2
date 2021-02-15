package jxsource.net.proxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PushbackInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogPipe implements Runnable{
	private static Logger log = LoggerFactory.getLogger(LogPipe.class);
	private InputStream in;
	
	public LogPipe(InputStream in) {
		this.in = in;
	}

	protected void proc() throws Exception {
		byte[] buf = new byte[1024*8];
		int i=0;
		while((i=in.read(buf))!=-1) {
			System.err.println(new String(buf,0,i));
		}
//		HttpMessageBuilder builder = new HttpMessageBuilder();
//				HttpMessage request = builder.extractMessage(in);
//				HttpMessage response = builder.extractMessage(in);
//				String str = String.format("\nrequest: %s\nresponse: %s", 
//						request, response);
//				log.debug(debugLog(str));
	}
	@Override
	public void run() {
		try {
			proc();
		} catch (Exception e) {
			String info = String.format("Exception: %s(%s)",
					e.getClass().getSimpleName(), e.getMessage());
			log.debug(debugLog(info));
		} finally {
			log.debug(debugLog("close"));
			try {
				in.close();
			} catch(IOException e) {}
		}
	}
	private String debugLog(String info) {
		String msg = String.format("*** Thread(%s): LogPipe(%d) %s",
				Thread.currentThread().getName(), this.hashCode(), info);
		return msg;
		
	}
}
