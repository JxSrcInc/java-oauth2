package jxsource.net.proxy;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * Pipe to pass data between client and server
 */
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

//	private boolean isTimeout(InputStream in) throws IOException, InterruptedException {
//		boolean timeout = true;
//		// wait first message up to timeout
//		for(int i=1; i<10; i++) {
//			debugLog(String.format("wait %d ms", 10*i));
//			if(in.available() > 0) {
//				timeout = false;
//				break;
//			} else {
//				Thread.sleep(10*i);
//			}
//		}
//		return timeout;
//	}
	public void run() {
		log.debug(getLogMsg("start"));
		Exception exception = new InterruptedException();
		try {
//			if(addTimeout && isTimeout(in)) {
//				throw new IOException("Input stream timeout.");
//			}
			byte[] buf = new byte[1024 * 8];
			int i = 0;
			boolean isLogOutReady = true;
			while ((i = in.read(buf)) != -1) {
				// cannot catch interruption when thread is blocked waiting to read
				// it just break the try but no exception is catched
				// don't understand why?
			    	out.write(buf, 0, i);
					out.flush();
					if(isLogOutReady) {
						try {
							logOut.write(buf, 0, i);
							logOut.flush();
						} catch(Exception e) {
							// turn logOut off
							isLogOutReady = false;
							log.error(getLogMsg("logOut Exception"), e);
						}
					}
			}
		} catch(Exception e) {
			exception = e;
		} finally {
			// Close LogOut Pipe to let LogOut Pipe reader terminate if it still works
			try {
				log.debug(getLogMsg("close log output stream"));
				logOut.close();
			} catch(IOException ioeOut) {}
			// InputStream in and OutputStream out are not closed here
			// but action fired on listener will cause Worker to close them
			// because in and out are for different socket. Worker will handle them better.
		}
		log.debug(getLogMsg("end - "+exception.toString()));
		// Notify Worker that this channel closes
		listener.actionPerformed(new ActionEvent(exception, 0, name));
	}
	
	private String getLogMsg(String info) {
		String msg = String.format("*** Thread(%s): %s(%d) %s",
				Thread.currentThread().getName(), name, this.hashCode(), info);
		return msg;
		
	}
}
