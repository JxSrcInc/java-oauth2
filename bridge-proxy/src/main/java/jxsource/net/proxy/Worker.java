package jxsource.net.proxy;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jxsource.net.proxy.util.ThreadUtil;

/*
 * Worker sets up input and output streams for 
 * 	PipeLocalToRemote, PipeRemoteToLocal and LogProcess
 * 
 * It also handles event actions fired from PipeLocalToRemote and PipeRemoteToLocal
 * when one pipe stopped and then stops the other by closing its input stream
 */
public class Worker implements Runnable, ActionListener {
	private static Logger logger = LoggerFactory.getLogger(Worker.class);
	private String PipeLocalToRemoteName; // = PipeLocalToRemote.class.getSimpleName();
	private String PipeRemoteToLocalName; // = PipeRemoteToLocal.class.getSimpleName();
	private Socket localSocket;
	private Socket remoteSocket;
	private volatile InputStream localInput;
	private volatile OutputStream localOutput;
	private volatile InputStream remoteInput;
	private volatile OutputStream remoteOutput;
	private PipeLocalToRemote pipeLocalToRemote;
	private PipeRemoteToLocal pipeRemoteToLocal;
	private Thread threadLocalToRemote;
	private Thread threadRemoteToLocal;

	// if logProcess is null, no threadLogProcess run
	private Log log;
	private boolean activeLog;
	private AtomicBoolean active = new AtomicBoolean(true);

	public Worker setLog(Log log, boolean activeLog) {
		this.log = log;
		this.activeLog = activeLog;
		return this;
	}

	public Worker setPipeRemoteToLocal(PipeRemoteToLocal pipeRemoteToLocal) {
		this.pipeRemoteToLocal = pipeRemoteToLocal;
		this.PipeRemoteToLocalName = pipeRemoteToLocal.getClass().getSimpleName();
		return this;
	}

	public Worker setPipeLocalToRemote(PipeLocalToRemote pipeLocalToRemote) {
		this.pipeLocalToRemote = pipeLocalToRemote;
		this.PipeLocalToRemoteName = pipeLocalToRemote.getClass().getSimpleName();
		return this;
	}

	public Worker init(Socket localSocket, InputStream localSocketInput, Socket remoteSocket)
			throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		this.localSocket = localSocket;
		this.remoteSocket = remoteSocket;
		logger.debug(debugInfo("starat"));
		if (localSocketInput != null) {
			this.localInput = localSocketInput;
		} else {
			this.localInput = localSocket.getInputStream();
		}
		localOutput = this.localSocket.getOutputStream();
		remoteInput = this.remoteSocket.getInputStream();
		remoteOutput = this.remoteSocket.getOutputStream();
		// create log pipe
		PipedOutputStream logOutClientToServer = null;
		PipedOutputStream logOutServerToClient = null;
		// create working pipe
		pipeLocalToRemote.init(PipeLocalToRemoteName, localSocketInput, remoteOutput, log, activeLog);
		pipeLocalToRemote.setListener(this);
		pipeRemoteToLocal.init(PipeRemoteToLocalName, remoteInput, localOutput, log, activeLog);
		pipeRemoteToLocal.setListener(this);
		return this;
	}

	@Override
	public void run() {
		try {
			threadLocalToRemote = ThreadUtil.createThread(pipeLocalToRemote);
			threadLocalToRemote.start();
			threadRemoteToLocal = ThreadUtil.createThread(pipeRemoteToLocal);
			threadRemoteToLocal.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public synchronized void actionPerformed(ActionEvent e) {
		// both PipeClientToServer and PipeServerToClient will call
		// but only the first one needs to process - close other pipe
		if (active.compareAndSet(true, false)) {
			String pipeName = e.getActionCommand();//e.getSource().getClass().getSimpleName();
			String msg = "Reciev event: " + e.getActionCommand() + " " + e.getSource().toString();
			System.err.println(pipeName+","+PipeLocalToRemoteName);
			logger.debug(debugInfo(msg));
			// handle remote socket timeout
				// stop thread
				if (pipeName.equals(PipeLocalToRemoteName)) {
					logger.debug(debugInfo(
							String.format("Interrupt %s(%d)", PipeRemoteToLocalName, threadRemoteToLocal.hashCode())));
					// the best way to close other pipe is close its input stream to release the
					// read block
					// it is better than using thread interrupt or java event
					try {
						remoteInput.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				} else {
					logger.debug(debugInfo(
							String.format("Interrupt %s(%d)", PipeLocalToRemoteName, threadLocalToRemote.hashCode())));
					// the best way to close other pipe is close its input stream to release the
					// read block
					// it is better than using thread interrupt or java event
					try {
						localInput.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
				logger.debug(debugInfo("destroy worker"));
				destroy();
		} else {
			System.err.println("Worker skip event process. - may remove later");
		}
	}

	/*
	 * Input streams and output streams are left for Pipes to close
	 * either when local or remote connection ends 
	 * or when Worker handles Pipe triggered event
	 */
	private void destroy() {
		closeSocket(localSocket);		
		closeSocket(remoteSocket);
		localSocket = null;
		localInput = null;
		localOutput = null;
		pipeLocalToRemote = null;
		threadLocalToRemote = null;		
		remoteSocket = null;
		remoteInput = null;
		remoteOutput = null;
		pipeRemoteToLocal = null;
		threadRemoteToLocal = null;
	}

	private String debugInfo(String msg) {
		String info = String.format("\n\t*** %s: Worker(%d), remoteSocket(%s:%d): %s", ThreadUtil.threadInfo(),
				this.hashCode(), 
				remoteSocket.getInetAddress().getHostName(), remoteSocket.getPort(), msg);
		return info;

	}

	private void closeSocket(Socket socket) {
		try {
			socket.close();
		} catch (IOException e) {
		}
	}

}
