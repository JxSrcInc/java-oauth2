package jxsource.net.proxy;

import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jxsource.net.proxy.http.HttpLogProcess;

public class LogProcessHttp extends LogProcess {
	private static Logger log = LoggerFactory.getLogger(LogProcessHttp.class);
	protected InputStream inClient;
	protected InputStream inServer;
//	protected HttpLogProcess clientHttpMessageProcess;
//	protected HttpLogProcess serverHttpMessageProcess;
	protected boolean clientReady = true;
	protected boolean serverReady = true;
	
	public LogProcessHttp() {
		
	}
	protected void init(InputStream inClient, InputStream inServer) {
		this.inClient = inClient;
		this.inServer = inServer;
	}

	@Override
	public void run() {
		log.debug(debugInfo("Http log started"));
		HttpLogProcess clientHttpMessageProcess = new HttpLogProcess().init(inClient);
		HttpLogProcess serverHttpMessageProcess = new HttpLogProcess().init(inServer);
		while(clientReady && serverReady) {
			try {
				clientHttpMessageProcess.proc();
			} catch (Exception e) {
				log.debug(debugInfo("clientHttpMessageProcess Exception"), e);
				try {
					inClient.close();
					clientReady = false;
				} catch (IOException e1) {
				}
			}
			try {
				serverHttpMessageProcess.proc();
			} catch (Exception e) {
				log.debug(debugInfo("serverHttpMessageProcess Exception"), e);
				serverReady = false;
				try {
					inServer.close();
				} catch (IOException e1) {
				}
			}
			if(clientReady && serverReady) {
			String msg = String.format(">>> Request:\n%s %s\n<<< Response:\n%s %s",
					new String(clientHttpMessageProcess.getHeaderBytes()),
					clientHttpMessageProcess.getContent()==null?"":new String(clientHttpMessageProcess.getContent()),
							new String(serverHttpMessageProcess.getHeaderBytes()),
							serverHttpMessageProcess.getContent()==null?"":new String(serverHttpMessageProcess.getContent())
							
					);
			log.info(debugInfo("****** Transfer ******\n"+msg));
			}
		}
		log.debug(debugInfo("thread terminate"));
	}
	
}
