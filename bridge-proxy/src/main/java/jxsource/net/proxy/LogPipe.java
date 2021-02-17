package jxsource.net.proxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.io.PushbackInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jxsource.net.proxy.http.HttpHeaderReader;
import jxsource.net.proxy.util.exception.HttpHeaderReaderException;

public class LogPipe implements Runnable{
	private static Logger log = LoggerFactory.getLogger(LogPipe.class);
	protected InputStream in;
	protected PrintStream ps;
	
	protected LogPipe(InputStream in, PrintStream...ps) {
		this.in = in;
		if(ps.length > 0) {
			this.ps = ps[0];
		} else {
			this.ps = System.out;
		}
	}

	protected void proc() throws Exception {
		if(in == null) return;
		byte[] buf = new byte[1024*8];
		int i=0;
		while((i=in.read(buf))!=-1) {
			ps.println(new String(buf,0,i));
		}
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
