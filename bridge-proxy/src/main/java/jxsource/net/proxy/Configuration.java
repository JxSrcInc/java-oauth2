package jxsource.net.proxy;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class Configuration {

	@Value("${proxy.log.process:jxsource.net.proxy.LogProcessPrint}")
	String logProcess;
	@Value("${proxy.tls.out-going.socket:false}")
	boolean tlsClient;
	@Value("${proxy.tls.in-coming.server-socket:false}")
	boolean tlsServer;
	@Value("${proxy.export-message:true}")
	boolean exportMessage;
	
	@PostConstruct
	public void init() {
		System.err.println("proxy.log.process="+logProcess);
		System.err.println("proxy.tls.out-going.socket="+tlsClient);
		System.err.println("proxy.tls.in-coming.server-socket:false="+tlsServer);
		System.err.println("proxy.export-message="+exportMessage);

		AppContext appContext = AppContext.get()
		.setTlsOutGoingSocket(tlsClient)
		.setTlsInComingServerSocket(tlsServer);
		try {
			appContext.setLogProcess(logProcess);
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}
