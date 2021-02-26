package jxsource.net.proxy;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.net.ServerSocketFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jxsource.net.proxy.util.ThreadUtil;

@Component
public class ProxySocketServer {
	private static Logger log = LoggerFactory.getLogger(ProxySocketServer.class);
	
	public void start() {
		AppContext appContext = AppContext.get();
		try {
			ServerSocket ss = appContext.getDefaultServerSocketFactory()
					.createServerSocket(appContext.getServerSocketPort());
			
			log.info("listening on " + ss.getInetAddress() + ":" + ss.getLocalPort() + " .....");
			while (true) {
				Socket localSocket = ss.accept();
				log.info("accept: " + localSocket.getInetAddress());
				try {
					Dispatcher dispatcher = new Dispatcher().init(localSocket, appContext.getAppType(),
							appContext.getRemoteDomain(), appContext.getRemotePort());
					ThreadUtil.createThread(dispatcher).start();
				} catch(Exception se) {
					log.error("fail to create Dispatcher thread",se);
					localSocket.close();
				}
			}
		} catch (IOException e) {
			log.error("Error: stop application", e);
			// TODO: use spring termination
			System.exit(1);;
		}
		

	}

}
