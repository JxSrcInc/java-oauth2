package jxsource.net.proxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PushbackInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jxsource.net.proxy.http.HttpHeaderReader;
import jxsource.net.proxy.util.exception.HttpHeaderReaderException;
import jxsource.net.proxy.http.HttpHeader;
import jxsource.net.proxy.http.HttpMessageProcess;

public class LogProcessHttp extends LogProcess {
	private static Logger log = LoggerFactory.getLogger(LogProcessHttp.class);
	protected InputStream inClient;
	protected InputStream inServer;
	protected HttpMessageProcess clientHttpMessageProcess;
	protected HttpMessageProcess serverHttpMessageProcess;
	
	public LogProcessHttp() {
		
	}
	protected void init(InputStream inClient, InputStream inServer) {
		this.inClient = inClient;
		this.inServer = inServer;
		clientHttpMessageProcess = new HttpMessageProcess().init(inClient);
		serverHttpMessageProcess = new HttpMessageProcess().init(inServer);
	}

	@Override
	public void run() {
		log.debug(debugLog("Http log started"));
		while(true) {
			try {
				clientHttpMessageProcess.proc();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				serverHttpMessageProcess.proc();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String msg = String.format(">>> Request:\n%s %s\n<<< Response:\n%s %s",
					new String(clientHttpMessageProcess.getHeaderBytes()),
					clientHttpMessageProcess.getContent()==null?"":new String(clientHttpMessageProcess.getContent()),
							new String(serverHttpMessageProcess.getHeaderBytes()),
							serverHttpMessageProcess.getContent()==null?"":new String(serverHttpMessageProcess.getContent())
							
					);
			log.debug("****** Transfer ******\n"+msg);
		}
	}
}
