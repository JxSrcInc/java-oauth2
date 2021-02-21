package jxsource.net.proxy;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jxsource.net.proxy.util.ThreadUtil;

/*
 * Pipe to pass data between client and server
 */
public abstract class Pipe implements Runnable {
	protected Logger log = LoggerFactory.getLogger(Pipe.class);
	protected InputStream in;
	protected OutputStream out;
	protected OutputStream logOut;
	protected String name;
	protected ActionListener listener;

	protected void init(String name, InputStream in, OutputStream out, OutputStream logOut) {
		this.name = name;
		this.in = in;
		this.out = out;
		// logOut may be null if no output requires 
		this.logOut = logOut;
	}

	public void setListener(ActionListener listener) {
		this.listener = listener;
	}

	protected abstract void proc() throws IOException;

	public void run() {
		log.debug(getLogMsg("start"));
		try {
			proc();
//			byte[] buf = new byte[1024 * 8];
//			int i = 0;
//			boolean isLogOutReady = true;
//			while ((i = in.read(buf)) != -1) {
//				out.write(buf, 0, i);
//				out.flush();
//				if (isLogOutReady && logOut != null) {
//					try {
//						logOut.write(buf, 0, i);
//						logOut.flush();
//					} catch (IOException e) {
//						// turn logOut off
//						isLogOutReady = false;
//						log.error(getLogMsg("logOut Exception"), e);
//					}
//				}
//			}
		} catch (IOException e) {
			log.debug(getLogMsg("Pipe input/output streamd error"), e);
			// Notify Worker that this channel closes
			listener.actionPerformed(new ActionEvent(this, 0, "Notify Worker that Pipe thread stop."));
		} finally {
			if (logOut != null) {
				// Close LogOut Pipe to let LogOut Pipe reader terminate if it still works
				try {
					log.debug(getLogMsg("close logOut stream"));
					logOut.close();
				} catch (IOException ioeOut) {
				}
			}
			try {
				in.close();
			} catch (IOException e1) {
			}
			try {
				out.close();
			} catch (IOException e1) {
			}

		}
		log.debug(getLogMsg("thread stop"));
	}

	protected String getLogMsg(String info) {
		String msg = String.format("*** %s: %s(%d) %s", ThreadUtil.threadInfo(), name, this.hashCode(), info);
		return msg;

	}

}
