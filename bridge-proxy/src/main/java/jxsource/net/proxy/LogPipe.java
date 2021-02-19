package jxsource.net.proxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogPipe implements Runnable {
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
		log.debug(debugInfo("finish"));
	}
	@Override
	public void run() {
		try {
			proc();
		} catch (Exception e) {
			String info = String.format("Exception: %s(%s)",
					e.getClass().getSimpleName(), e.getMessage());
			log.debug(debugInfo(info));
		} finally {
			log.debug(debugInfo("close"));
			try {
				in.close();
			} catch(IOException e) {}
		}
		log.debug(debugInfo("thread stop"));
	}
	private String debugInfo(String info) {
		String msg = String.format("*** Thread(%s): LogPipe(%d) %s",
				Thread.currentThread().getName(), this.hashCode(), info);
		return msg;
		
	}

}
