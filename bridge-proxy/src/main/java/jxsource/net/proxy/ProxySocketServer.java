package jxsource.net.proxy;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ProxySocketServer {
	private static Logger log = LoggerFactory.getLogger(ProxySocketServer.class);
	
	@Value("${proxy.bridge:false}")
	private boolean bridge;
	// remote host to connect
	@Value("${proxy.server.host}")
	private String serverHost;
	// remote port to connect
	@Value("${proxy.server.port}")
	private int serverPort;
	// socket server listening port
	@Value("${socketserver.port:9999}")
	private int port;
//	@Autowired
//	private Dispatcher dispatcher;
	
	public void start() {
		Configuration.config();
		try {
			ServerSocket ss = new ServerSocket(port);
			
			log.info("listening on " + ss.getInetAddress() + ":" + ss.getLocalPort() + " .....");
			while (true) {
				Socket client = ss.accept();
				log.info("accept: " + client.getInetAddress());
				try {
					Dispatcher dispatcher = new Dispatcher().init(client, bridge, serverHost, serverPort);
				new Thread(dispatcher).start();
				} catch(Exception se) {
					log.error("fail to create Dispatcher thread",se);
					client.close();
				}
			}
		} catch (IOException e) {
			log.error("Error: stop application", e);
			// TODO: use spring termination
			System.exit(1);;
		}
		

	}

}
