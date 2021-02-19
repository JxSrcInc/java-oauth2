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

//@Component
//@Scope("prototype")
public class Worker implements Runnable, ActionListener{
	private static Logger log = LoggerFactory.getLogger(Worker.class);
	private static final String pipeClientToServerName = "pipeClientToServer";
	private static final String pipeServerToClientName = "pipeServerToClient";
	private Socket client;
	private Socket server;
	private InputStream clientInput;
	private OutputStream clientOutput;
	private InputStream serverInput;
	private OutputStream serverOutput;
	private Pipe pipeClientToServer;
	private Pipe pipeServerToClient;
	private Thread threadClientToServer;
	private Thread threadServerToClient;
	
	private Thread threadLogProcess;
	
	private LogContext context = LogContext.get();
	private LogProcess logProcess;
	AtomicBoolean active = new AtomicBoolean(true);
	
	public Worker init(Socket client, InputStream clientInput, Socket server) 
			throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException{
		this.client = client;
		this.server = server;
		log.debug(debugInfo("starat"));
		if(clientInput != null) {
			this.clientInput = clientInput;
		} else {
			this.clientInput = client.getInputStream();
		}
		clientOutput = this.client.getOutputStream();
		serverInput = this.server.getInputStream();
		serverOutput = this.server.getOutputStream();
		// create log pipe
		PipedOutputStream logOutClientToServer = new PipedOutputStream();
		PipedInputStream logInputStreamClientToServer  = new PipedInputStream(logOutClientToServer);
		PipedOutputStream logOutServerToClient = new PipedOutputStream();
		PipedInputStream logInputStreamServerToClient  = new PipedInputStream(logOutServerToClient);
		// create working pipe
		pipeClientToServer = new PipeClientToServer(pipeClientToServerName, clientInput, serverOutput, logOutClientToServer);
		pipeClientToServer.setListener(this);
		pipeServerToClient = new PipeServerToClient(pipeServerToClientName, serverInput, clientOutput, logOutServerToClient);
		pipeServerToClient.setListener(this);
		// create log process
		logProcess = context.getLogProcess();
		logProcess.init(logInputStreamClientToServer, logInputStreamServerToClient);
		return this;
	}

	@Override
	public void run() {
		try {
			threadLogProcess = ThreadUtil.createThread(logProcess);
			threadLogProcess.start();
			threadClientToServer = ThreadUtil.createThread(pipeClientToServer);
			threadClientToServer.start();
			threadServerToClient = ThreadUtil.createThread(pipeServerToClient);
			threadServerToClient.start();
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
		String pipeName = e.getActionCommand();
		String msg = "ActionPerformed: "+e.getActionCommand()+" "+e.getSource().toString();
		log.debug(debugInfo(msg));
		// stop thread
		if(pipeName.equals(pipeClientToServerName)) {
				log.debug(debugInfo(String.format("Interrupt %s(%d)", pipeServerToClientName, threadServerToClient.hashCode())));
				// the best way to close other pipe is close its input stream to release the read block
				// it is better than using thread interrupt or java event
				try {
					serverInput.close();
				} catch (IOException e1) {}
		} else {
				log.debug(debugInfo(String.format("Interrupt %s(%d)", pipeClientToServerName, threadClientToServer.hashCode())));
				// the best way to close other pipe is close its input stream to release the read block
				// it is better than using thread interrupt or java event
				try {
					clientInput.close();
				} catch (IOException e1) {}
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
		clientInput = null;
		clientOutput = null;
		serverInput = null;
		serverOutput = null;
		pipeClientToServer = null;
		pipeServerToClient = null;
		threadClientToServer = null;
		threadServerToClient = null;
		
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
