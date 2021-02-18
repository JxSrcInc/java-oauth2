package jxsource.net.proxy;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
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
	
	public Worker init(Socket client, InputStream clientInput, Socket server) throws IOException, InstantiationException, IllegalAccessException{
		String msg = String.format("*** Thread(%s): start Worker(%d), client(%s:%d), server(%s:%d)",
				Thread.currentThread().getName(), this.hashCode(), 
				client.getInetAddress().getHostName(), client.getPort(),
				server.getInetAddress().getHostName(), server.getPort());
		log.debug(msg);
		this.client = client;
		this.server = server;
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
		pipeClientToServer = new Pipe(pipeClientToServerName, clientInput, serverOutput, logOutClientToServer, true);
		pipeClientToServer.setListener(this);
		pipeServerToClient = new Pipe(pipeServerToClientName, serverInput, clientOutput, logOutServerToClient);
		pipeServerToClient.setListener(this);
		// create log process
		logProcess = context.getLogProcess();
		logProcess.init(logInputStreamClientToServer, logInputStreamServerToClient);
		return this;
	}

	@Override
	public void run() {
		try {
			threadLogProcess = new Thread(logProcess);
			threadLogProcess.start();
			threadClientToServer = new Thread(pipeClientToServer);
			threadClientToServer.start();
			threadServerToClient = new Thread(pipeServerToClient);
			threadServerToClient.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String pipeName = e.getActionCommand();
		String msg = "ActionPerformed: "+e.getActionCommand()+" "+e.getSource().toString();
		debugLog(msg);
		// stop thread
		if(pipeName.equals(pipeClientToServerName)) {
			// TODO: check transaction complete?
			if(threadServerToClient.isAlive()) {
				debugLog(String.format("Interrupt %s(%d)", pipeServerToClientName, threadServerToClient.hashCode()));
				threadServerToClient.interrupt();
			} else {
				destroy();
			}
		} else {
			if(threadClientToServer.isAlive()) {
				debugLog(String.format("Interrupt %s(%d)", pipeClientToServerName, threadClientToServer.hashCode()));
				threadClientToServer.interrupt();
			} else {
				destroy();
			}
		}
	}
	private void destroy() {
		// close sockets
		debugLog("finished");
		// TODO: Do we need to close socket's input and output stream?
		closeSocket(client);
		closeSocket(server);
	}
	private void debugLog(String info) {
		String msg = String.format("*** Thread(%s): Worker(%d), client(%s:%d), server(%s:%d): %s",
				Thread.currentThread().getName(), this.hashCode(), 
				client.getInetAddress().getHostName(), client.getPort(),
				server.getInetAddress().getHostName(), server.getPort(),
				info);
		log.debug(msg);
		
	}

	private void closeInputStream(InputStream stream) {
		try {
			stream.close();
		} catch(IOException e) {
		}
	}
	private void closeOutputStream(OutputStream stream) {
		try {
			stream.close();
		} catch(IOException e) {
		}
	}
	private void closeSocket(Socket socket) {
		try {
			socket.close();
		} catch(IOException e) {
		}
	}

}
