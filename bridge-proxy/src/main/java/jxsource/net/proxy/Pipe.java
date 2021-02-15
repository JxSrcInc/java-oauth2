package jxsource.net.proxy;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Pipe implements Runnable {
	private static Logger log = LoggerFactory.getLogger(Pipe.class);
	private InputStream in;
	private OutputStream out;
	private OutputStream logOut;
	private String name;
	private ActionListener listener;
	private boolean addTimeout;

	public Pipe(String name, InputStream in, OutputStream out, OutputStream logOut) {
		this(name, in, out, logOut, false);
	}

	public Pipe(String name, InputStream in, OutputStream out, OutputStream logOut, boolean addTimeout) {
		this.name = name;
		this.in = in;
		this.out = out;
		this.logOut = logOut;
		this.addTimeout = addTimeout;
	}
	
	public void setListener(ActionListener listener) {
		this.listener = listener;
	}

	private boolean isTimeout(InputStream in) throws IOException, InterruptedException {
		boolean timeout = true;
		// wait first message up to timeout
		for(int i=1; i<10; i++) {
			debugLog(String.format("wait %d ms", 10*i));
			if(in.available() > 0) {
				timeout = false;
				break;
			} else {
				Thread.sleep(10*i);
			}
		}
		return timeout;
	}
	public void run() {
		debugLog("start");
		Exception exception = new InterruptedException();
		try {
//			if(addTimeout && isTimeout(in)) {
//				throw new IOException("Input stream timeout.");
//			}
			byte[] buf = new byte[1024 * 8];
			int i = 0;
			while ((i = in.read(buf)) != -1) {
				// cannot catch interruption when thread is blocked waiting to read
				// it just break the try but no exception is catched
				// don't understand why?
			    	out.write(buf, 0, i);
					out.flush();
					if(logOut != null) {
						// TODO: need additional logOut check?
						logOut.write(buf, 0, i);
						logOut.flush();
					}
			}
		} catch(Exception e) {
			exception = e;
		} finally {
			// Close LogPipe to let LogPipe.proc() complete normally.
			// If LogPipe is not closed here, the end of Pipe will result in 
			// LogPipe.proc() throws IOException due to block read in.read broken.
			// Either way works.
			try {
				debugLog("close log output stream");
				logOut.close();
			} catch(IOException ioeOut) {}
		}
		debugLog("end - "+exception.toString());
		listener.actionPerformed(new ActionEvent(exception, 0, name));
	}
	
	private void debugLog(String info) {
		String msg = String.format("*** Thread(%s): %s(%d) %s",
				Thread.currentThread().getName(), name, this.hashCode(), info);
		log.debug(msg);
		
	}
}
