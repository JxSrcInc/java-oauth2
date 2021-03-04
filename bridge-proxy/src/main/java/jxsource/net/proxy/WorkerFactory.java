package jxsource.net.proxy;

import jxsource.net.proxy.http.HttpRequestEditor;
import jxsource.net.proxy.http.HttpContext;
import jxsource.net.proxy.http.HttpResponseEditor;
import jxsource.net.proxy.http.HttpWorker;

public class WorkerFactory {

	private AppContext appContext = AppContext.get();

	public static WorkerFactory build() {
		return new WorkerFactory();
	}


	public Worker create(String remoteHost, int remotePort) {
		String connType = appContext.getConnType();
		switch(connType) {
		case Constants.ConnTcpType:
			return new PipeWorker().setPipeLocalToRemote(new PipeLocalToRemote())
					.setPipeRemoteToLocal(new PipeRemoteToLocal())
					.setLog(appContext.getLog(), appContext.isHttpBodyLog());
		case Constants.CoonHttpType:	
			return new HttpWorker()
					.setLog(appContext.getLog());
			default:
				throw new RuntimeException("invalide connType: "+connType);
		}
	}

}
