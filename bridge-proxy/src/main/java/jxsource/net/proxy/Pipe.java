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
 * Pipe to pass data between local socket and remote socket
 */
public abstract class Pipe implements Runnable {
	protected Logger logger = LoggerFactory.getLogger(Pipe.class);
	protected InputStream in;
	protected OutputStream out;
	protected Log log;
	protected String name;
	protected ActionListener listener;
	protected boolean activeLog = false;

	protected void init(String name, InputStream in, OutputStream out, Log log, boolean activeLog) {
		this.name = name;
		this.in = in;
		this.out = out;
		// logOut may be null if no output requires
		this.log = log;
		this.activeLog = activeLog;
	}

	public void setListener(ActionListener listener) {
		this.listener = listener;
	}

	protected abstract void proc() throws IOException;

	public void run() {
		logger.debug(getLogMsg("start"));
		try {
			proc();
		} catch (IOException e) {
			logger.debug(getLogMsg(name+" IOException"), e);
			// Notify Worker that this channel closes
			listener.actionPerformed(new ActionEvent(e, 0, this.getClass().getSimpleName()));
		} finally {
			try {
				in.close();
			} catch (IOException e1) {
			}
			try {
				out.close();
			} catch (IOException e1) {
			}
			log = null;
		}
		logger.debug(getLogMsg("thread stop"));
	}

	protected String getLogMsg(String info) {
		String msg = String.format("\n\t*** %s: %s(%d) %s", ThreadUtil.threadInfo(), name, this.hashCode(), info);
		return msg;

	}

}
