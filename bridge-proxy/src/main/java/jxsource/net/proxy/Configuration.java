package jxsource.net.proxy;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class Configuration {

	@Value("${proxy.log:jxsource.net.proxy.PrintLog}")
	String log;
	@Value("${proxy.tls.out-going.socket:false}")
	boolean tlsOutGoing;
	@Value("${proxy.tls.in-coming.socket:false}")
	boolean thsInComing;
	
	@Value("${proxy.app.type:bridge}")
	private String appType;

	@Value("${proxy.tcp.log:true}")
	boolean tcpLog;
	@Value("${proxy.connect.type:tcp}")
	private String connType;
	
	// remote host to connect
	@Value("${proxy.remote.domain}")
	private String remoteDomain;
	// remote port to connect
	@Value("${proxy.remote.port}")
	private int remotePort;
	// socket server listening port
	@Value("${proxy.server.port}")
	private int serverPort;

	
	@PostConstruct
	public void init() {
		System.err.println("proxy.log="+log);
		System.err.println("proxy.tls.out-going.socket="+tlsOutGoing);
		System.err.println("proxy.tls.in-coming.socket="+thsInComing);
		
		System.err.println("proxy.app.type="+appType);

		System.err.println("proxy.http.body-log="+tcpLog);
		System.err.println("proxy.connect.type="+connType);

		System.err.println("proxy.remote.domain="+remoteDomain);
		System.err.println("proxy.remote.port="+remotePort);
		System.err.println("proxy.server.port="+serverPort);

		try {
			AppContext.get()
			.setTlsOutGoingSocket(tlsOutGoing)
			.setTlsInComingServerSocket(thsInComing)
			.setTcpLog(tcpLog)
			.setAppType(appType)
			.setConnType(connType)
			.setRemoteDomain(remoteDomain)
			.setRemotePort(remotePort)
			.setServerSocketPort(serverPort);

//			.setLog(log);
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: refine
			System.exit(1);
		}
	}
}
