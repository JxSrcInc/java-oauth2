package jxsource.net.proxy;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.Socket;
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
public class Worker implements Runnable, ActionListener{
	private static Logger log = LoggerFactory.getLogger(Worker.class);
	private String PipeLocalToRemoteName; //= PipeLocalToRemote.class.getSimpleName();
	private String PipeRemoteToLocalName; //= PipeRemoteToLocal.class.getSimpleName();
	private Socket client;
	private Socket server;
	private volatile InputStream localInput;
	private volatile OutputStream localOutput;
	private volatile InputStream remoteInput;
	private volatile OutputStream remoteOutput;
	private PipeLocalToRemote pipeLocalToRemote;
	private PipeRemoteToLocal pipeRemoteToLocal;
	private Thread threadLocalToRemote;
	private Thread threadRemoteToLocal;
	
	// if logProcess is null, no threadLogProcess run
	private LogProcess logProcess;
	private Thread threadLogProcess;

	private AtomicBoolean active = new AtomicBoolean(true);
	
	public Worker setLogProcess(LogProcess logProcess) {
		this.logProcess = logProcess;
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
	
	public Worker init(Socket client, InputStream clientInput, Socket server) 
			throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException{
		this.client = client;
		this.server = server;
		log.debug(debugInfo("starat"));
		if(clientInput != null) {
			this.localInput = clientInput;
		} else {
			this.localInput = client.getInputStream();
		}
		localOutput = this.client.getOutputStream();
		remoteInput = this.server.getInputStream();
		remoteOutput = this.server.getOutputStream();
		// create log pipe
		PipedOutputStream logOutClientToServer = null;
		PipedOutputStream logOutServerToClient = null;
		// if logProcess is null, no threadLogProcess run
		if(logProcess != null) {
		logOutClientToServer = new PipedOutputStream();
		PipedInputStream logInputStreamClientToServer  = new PipedInputStream(logOutClientToServer);
		logOutServerToClient = new PipedOutputStream();
		PipedInputStream logInputStreamServerToClient  = new PipedInputStream(logOutServerToClient);
		// create log process
		logProcess.init(logInputStreamClientToServer, logInputStreamServerToClient);
		}
		// create working pipe
		pipeLocalToRemote.init(PipeLocalToRemoteName, clientInput, remoteOutput, logOutClientToServer);
		pipeLocalToRemote.setListener(this);
		pipeRemoteToLocal.init(PipeRemoteToLocalName, remoteInput, localOutput, logOutServerToClient);
		pipeRemoteToLocal.setListener(this);
		return this;
	}

	@Override
	public void run() {
		try {
			if(logProcess != null) {
				threadLogProcess = ThreadUtil.createThread(logProcess);
				threadLogProcess.start();
			}
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
		if(active.compareAndSet(true,  false)) {
		String pipeName = e.getSource().getClass().getSimpleName();
		String msg = "ActionPerformed: "+e.getActionCommand()+" "+e.getSource().toString();
		log.debug(debugInfo(msg));
		// stop thread
		if(pipeName.equals(PipeLocalToRemoteName)) {
				log.debug(debugInfo(String.format("Interrupt %s(%d)", PipeRemoteToLocalName, threadRemoteToLocal.hashCode())));
				// the best way to close other pipe is close its input stream to release the read block
				// it is better than using thread interrupt or java event
				try {
					remoteInput.close();
				} catch (IOException e1) {e1.printStackTrace();}
		} else {
				log.debug(debugInfo(String.format("Interrupt %s(%d)", PipeLocalToRemoteName, threadLocalToRemote.hashCode())));
				// the best way to close other pipe is close its input stream to release the read block
				// it is better than using thread interrupt or java event
				try {
					localInput.close();
				} catch (IOException e1) {e1.printStackTrace();}
		}
		destroy();
		} else {
//			System.err.println("Worker skip action");
		}
	}
	private void destroy() {
		// close sockets
		log.debug(debugInfo("destroy Worker and close sockets"));
		closeSocket(client);
		closeSocket(server);

		client = null;
		server = null;
		localInput = null;
		localOutput = null;
		remoteInput = null;
		remoteOutput = null;
		pipeLocalToRemote = null;
		pipeRemoteToLocal = null;
		threadLocalToRemote = null;
		threadRemoteToLocal = null;
		
		threadLogProcess = null;
		logProcess = null;
	}
	private String debugInfo(String msg) {
		String info = String.format("*** %s: Worker(%d), client(%s:%d), server(%s:%d): %s",
				ThreadUtil.threadInfo(), this.hashCode(), 
				client.getInetAddress().getHostName(), client.getPort(),
				server.getInetAddress().getHostName(), server.getPort(),
				msg);
		return info;
		
	}

	private void closeSocket(Socket socket) {
		try {
			socket.close();
		} catch(IOException e) {
		}
	}

}
