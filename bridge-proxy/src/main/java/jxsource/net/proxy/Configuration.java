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
	@Value("${proxy.tls.in-coming.server-socket:false}")
	boolean thsInComing;
	
	@Value("${proxy.app.type:bridge}")
	private String appType;

	@Value("${proxy.http.body-log:true}")
	boolean httpBodyLog;
	@Value("${proxy.connect.type:tcp}")
	private String connType;
	
	@PostConstruct
	public void init() {
		System.err.println("proxy.log="+log);
		System.err.println("proxy.tls.out-going.socket="+tlsOutGoing);
		System.err.println("proxy.tls.in-coming.server-socket:false="+thsInComing);
		
		System.err.println("proxy.app.type="+appType);

		System.err.println("proxy.http.body-log="+httpBodyLog);
		System.err.println("proxy.connect.type="+connType);

		try {
			AppContext.get()
			.setTlsOutGoingSocket(tlsOutGoing)
			.setTlsInComingServerSocket(thsInComing)
			.setHttpBodyLog(httpBodyLog)
			.setAppType(appType)
			.setConnType(connType)

			.setLog(log);
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: refine
			System.exit(1);
		}
	}
}
